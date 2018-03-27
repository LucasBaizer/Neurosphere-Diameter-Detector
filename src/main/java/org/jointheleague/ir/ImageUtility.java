package org.jointheleague.ir;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

public class ImageUtility {
	public static BufferedImage toBufferedImage(IplImage image) {
		OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
		Java2DFrameConverter java2dConverter = new Java2DFrameConverter();
		Frame frame = grabberConverter.convert(image);
		return java2dConverter.getBufferedImage(frame, 1);
	}

	public static BufferedImage scale(BufferedImage in, int w, int h) {
		BufferedImage out = new BufferedImage(w, h, in.getType());
		Graphics2D g = out.createGraphics();
		AffineTransform at = AffineTransform.getScaleInstance(w / (double) in.getWidth(), (double) h / in.getHeight());
		g.drawRenderedImage(in, at);
		return out;
	}

	public static IplImage toIplImage(BufferedImage image) {
		OpenCVFrameConverter.ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();
		Java2DFrameConverter java2dConverter = new Java2DFrameConverter();
		IplImage out = iplConverter.convert(java2dConverter.convert(image));
		return out;
	}
}
