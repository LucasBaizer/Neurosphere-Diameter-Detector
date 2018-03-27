package org.jointheleague.ir;

import java.awt.Point;

public class Detection {
	private Point center;
	private int radius;

	public Detection(Point center, int radius) {
		this.center = center;
		this.radius = radius;
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getDiameter() {
		return radius * 2;
	}

	public void setDiameter(int diameter) {
		this.radius = diameter / 2;
	}
}
