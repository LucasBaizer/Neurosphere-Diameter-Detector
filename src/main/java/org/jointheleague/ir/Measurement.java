package org.jointheleague.ir;

import java.util.HashMap;

public enum Measurement {
	PIXELS("pixels", "px"), MICROMETERS("micrometers", "μm");

	public static final HashMap<Conversion, Fraction> CONVERSIONS = new HashMap<>();

	static {
		// Fraction p2m = new Fraction(100, 55);
		Fraction p2m = Fraction.parseFraction(Cache.get("pixels->micrometers"));
		CONVERSIONS.put(new Conversion(PIXELS, Measurement.MICROMETERS), p2m);
		CONVERSIONS.put(new Conversion(MICROMETERS, Measurement.PIXELS), p2m.reciprocal());
	}

	private String unitName;
	private String fullName;

	private Measurement(String full, String unit) {
		this.fullName = full;
		this.unitName = unit;
	}

	public double convert(Measurement to, double x) {
		if (this == to) {
			return x;
		}

		return new Conversion(this, to).convert(x);
	}

	public static void setConversion(Conversion conversion, Fraction amount) {
		CONVERSIONS.put(conversion, amount);
		CONVERSIONS.put(conversion.inverse(), amount.reciprocal());

		Cache.save(conversion.getFrom().getFullName() + "->" + conversion.getTo().getFullName(), amount.toString());
		Cache.save(conversion.getTo().getFullName() + "->" + conversion.getFrom().getFullName(),
				amount.reciprocal().toString());
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
