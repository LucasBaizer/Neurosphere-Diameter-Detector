package org.jointheleague.org.jointheleague.ir;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvPoint2D32f;
import org.bytedeco.javacpp.opencv_core.CvPoint3D32f;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.CvSize;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc.CvFont;

public class App {
	/*
	 * public static void main(String[] args) { final int maxRadius = 75;
	 * 
	 * String imageName = "cells.jpg"; String imageOut = "cells-out.jpg";
	 * 
	 * Mat mat = imread(imageName); Mat greyscale = new Mat(); cvtColor(mat,
	 * greyscale, CV_BGR2GRAY); GaussianBlur(greyscale, greyscale, new Size(9, 9),
	 * 1d);
	 * 
	 * cvSaveImage(imageOut, new IplImage(greyscale));
	 * 
	 * Mat circles = new Mat(); HoughCircles(greyscale, circles, CV_HOUGH_GRADIENT,
	 * 1, greyscale.rows() / 8, 20, 50, 50, 10, maxRadius);
	 * 
	 * if (circles.arrayData() == null) {
	 * System.out.println("No circles detected."); return; }
	 * 
	 * System.out.println(circles.cols() / 3 + " circle(s) detected.");
	 * 
	 * FloatRawIndexer indexer = circles.createIndexer(); for (int i = 0; i <
	 * circles.cols(); i += 3) { int centerX = (int) indexer.get(0, i); int centerY
	 * = (int) indexer.get(0, i + 1); int radius = (int) indexer.get(0, i + 2);
	 * 
	 * circle(mat, new Point(centerX, centerY), 3, new Scalar(0d, 255d, 0d, 1d), -1,
	 * 8, 0); circle(mat, new Point(centerX, centerY), radius, new Scalar(0d, 255d,
	 * 0d, 1d), 1, 8, 0); }
	 * 
	 * IplImage img = new IplImage(mat); cvSaveImage(imageOut, img); }
	 */

	public static void main(String[] args) {
		IplImage src = cvLoadImage("cells.jpg");
		IplImage copy = cvLoadImage("cells.jpg");
		IplImage gray = cvCreateImage(src.cvSize(), 8, 1);

		cvCvtColor(src, gray, CV_BGR2GRAY);
		Mat grayMat = new Mat(gray);
		GaussianBlur(grayMat, grayMat, new Size(9, 9), 1d);
		gray = new IplImage(grayMat);

		CvMemStorage mem = CvMemStorage.create();

		CvSeq circles = cvHoughCircles(gray, // Input image
				mem, // Memory Storage
				CV_HOUGH_GRADIENT, // Detection method
				1, // Inverse ratio
				40, // Minimum distance between the centers of the detected circles
				100, // Higher threshold for canny edge detector
				25, // Threshold at the center detection stage
				15, // min radius
				50 // max radius
		);

		int total = 0;
		for (int i = 0; i < circles.total(); i++) {
			CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(circles, i));
			CvPoint center = cvPointFrom32f(new CvPoint2D32f(circle.x(), circle.y()));
			int radius = Math.round(circle.z());
			cvCircle(copy, center, radius, CvScalar.GREEN, 1, CV_AA, 0);

			CvFont font = cvFont(radius / 30d, 1);
			CvSize size = new CvSize();
			String text = Integer.toString(pixelsToMicrometers(radius * 2)) + "um";
			cvGetTextSize(text, font, size, new int[1]);

			cvPutText(copy, text, new CvPoint((int) circle.x() - (size.width() / 2), (int) circle.y()), font,
					CvScalar.GREEN);
			total += radius;
			circle.close();
		}

		CvFont font = cvFont(1, 1);
		CvSize size = new CvSize();
		String text = "Average diameter: " + pixelsToMicrometers((int) (total / (double) circles.total()) * 2) + "um";
		cvGetTextSize(text, font, size, new int[1]);

		cvPutText(copy, text, new CvPoint(src.width() - size.width(), src.height() - size.height()), font,
				CvScalar.YELLOW);

		cvSaveImage("cells-out.jpg", copy);
		try {
			Desktop.getDesktop().browse(new File("cells-out.jpg").toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int pixelsToMicrometers(int pixels) { // 55px == 100um
		return pixels * (100 / 55);
	}
}
