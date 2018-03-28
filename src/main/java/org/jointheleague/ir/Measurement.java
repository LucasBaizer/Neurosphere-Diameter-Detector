package org.jointheleague.ir;

import java.util.HashMap;

public enum Measurement {
	PIXELS, MICROMETERS;

	public static final HashMap<Conversion, Fraction> CONVERSIONS = new HashMap<>();

	static {
		Fraction p2m = new Fraction(100, 55);
		CONVERSIONS.put(new Conversion(PIXELS, Measurement.MICROMETERS), p2m);
		CONVERSIONS.put(new Conversion(MICROMETERS, Measurement.PIXELS), p2m.reciprocal());
	}

	public double convert(Measurement to, double x) {
		if (this == to) {
			return x;
		}

		return new Conversion(this, to).convert(x);
	}
}
