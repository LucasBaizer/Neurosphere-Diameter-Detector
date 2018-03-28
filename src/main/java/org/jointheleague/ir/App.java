package org.jointheleague.ir;

import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSize;
import org.bytedeco.javacpp.opencv_imgproc.CvFont;

public class App {
	public static void main(String[] args) {
		Detector detector = new Detector(new File("cells-highres.jpg"), 1d, 50d, 40d);
		try {
			detector.initialize();
		} catch (IOException e) {
			Program.exit(e);
		}
		detector.blur();
		List<Detection> result = detector.detect();
		int totalDiameter = 0;
		for (Detection det : result) {
			totalDiameter += det.getDiameter();
		}
		int average = (int) (totalDiameter / (double) result.size());

		CvFont font = cvFont(1, 1);
		CvSize size = new CvSize();
		String text = "Average diameter: " + Measurement.PIXELS.convert(Measurement.MICROMETERS, average) + "um";
		cvGetTextSize(text, font, size, new int[1]);

		cvPutText(detector.getOutput(), text,
				new CvPoint(detector.getOutput().width() - size.width(), detector.getOutput().height() - size.height()),
				font, CvScalar.YELLOW);

		cvSaveImage("cells-out.jpg", detector.getOutput());
		try {
			Desktop.getDesktop().browse(new File("cells-out.jpg").toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
