package org.jointheleague.ir;

public enum Measurement {
	PIXELS, MICROMETERS;

	public int to(Measurement to, int x) {
		if (this == to) {
			return x;
		}

		if (this == PIXELS) {
			if (to == MICROMETERS) {
				return (int) (x * (100 / 55d));
			}
		} else if (this == MICROMETERS) {
			if (to == PIXELS) {
				return (int) (x * (55 / 100d));
			}
		}

		throw new IllegalArgumentException(
				"Illegal conversion: " + this.name().toLowerCase() + " to " + to.name().toLowerCase());
	}
}
