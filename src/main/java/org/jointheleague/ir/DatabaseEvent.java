package org.jointheleague.ir;

import java.io.File;

public class DatabaseEvent {
	public static final int ADD = 0;
	public static final int MODIFY = 1;
	public static final int REMOVE = 2;

	private File imageFile;
	private DetectionList detections;
	private int type;

	public DatabaseEvent(File imageFile, DetectionList detections, int type) {
		this.imageFile = imageFile;
		this.detections = detections;
		this.type = type;
	}

	public File getImageFile() {
		return imageFile;
	}

	public void setImageFile(File imageFile) {
		this.imageFile = imageFile;
	}

	public DetectionList getDetections() {
		return detections;
	}

	public void setDetections(DetectionList detections) {
		this.detections = detections;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
