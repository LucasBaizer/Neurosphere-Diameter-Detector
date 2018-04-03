package org.jointheleague.ir;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvPoint2D32f;
import org.bytedeco.javacpp.opencv_core.CvPoint3D32f;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;

public class Detector {
	private File file;
	private double blur;
	private double minSize;
	private double minDistance;

	private IplImage src;
	private IplImage gray;
	private IplImage blurred;
	private IplImage output;

	private Subject<IplImage> onBlur = new Subject<IplImage>();

	public Detector(File file, double blur, double minSize, double minDistance) {
		this.file = file;
		this.blur = blur;
		this.minSize = minSize;
		this.minDistance = minDistance;
	}

	public void initialize() throws IOException {
		this.src = cvLoadImage(file.getAbsolutePath());
		this.output = cvLoadImage(file.getAbsolutePath());
		this.gray = cvCreateImage(src.cvSize(), 8, 1);
	}

	public void blur() {
		cvCvtColor(src, gray, CV_BGR2GRAY);

		Mat grayMat = new Mat(gray);
		if (blur > 0.0)
			GaussianBlur(grayMat, grayMat, new Size(9, 9), blur);

		this.blurred = new IplImage(grayMat);

		onBlur.markChanged();
		onBlur.notifyObservers(blurred);
	}

	public DetectionList detect() {
		int minSizePixels = (int) Measurement.MICROMETERS.convert(Measurement.PIXELS, minSize);

		CvMemStorage mem = CvMemStorage.create();
		CvSeq circles = cvHoughCircles(blurred, mem, CV_HOUGH_GRADIENT, 1,
				Measurement.MICROMETERS.convert(Measurement.PIXELS, minDistance), 100, 25, minSizePixels / 2,
				(int) (minSizePixels * 0.75));
		DetectionList detections = new DetectionList();

		BufferedImage img = ImageUtility.toBufferedImage(src);
		Graphics g = img.createGraphics();
		for (int i = 0; i < circles.total(); i++) {
			CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(circles, i));
			CvPoint center = cvPointFrom32f(new CvPoint2D32f(circle.x(), circle.y()));
			int radius = Math.round(circle.z());

			g.drawOval(center.x() - radius, center.y() - radius, radius * 2, radius * 2);
			detections.add(new Detection(new java.awt.Point(center.x(), center.y()), radius));

			circle.close();
		}

		return detections;
	}

	public IplImage getBlurred() {
		return blurred;
	}

	public void setBlurred(IplImage blurred) {
		this.blurred = blurred;
	}

	public IplImage getGrayscale() {
		return gray;
	}

	public void getGrayscale(IplImage gray) {
		this.gray = gray;
	}

	public IplImage getSource() {
		return src;
	}

	public void getSource(IplImage src) {
		this.src = src;
	}

	public IplImage getOutput() {
		return output;
	}

	public void setOutput(IplImage output) {
		this.output = output;
	}

	public Subject<IplImage> onBlur() {
		return onBlur;
	}
}
