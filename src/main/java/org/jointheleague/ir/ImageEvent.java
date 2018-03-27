package org.jointheleague.ir;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImageEvent {
	private File sourceFile;
	private BufferedImage image;

	public ImageEvent(File sourceFile, BufferedImage image) {
		this.sourceFile = sourceFile;
		this.image = image;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
}
