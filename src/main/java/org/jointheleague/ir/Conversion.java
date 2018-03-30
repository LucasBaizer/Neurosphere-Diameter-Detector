package org.jointheleague.ir;

public class Conversion {
	private Measurement from;
	private Measurement to;

	public Conversion(Measurement from, Measurement to) {
		this.from = from;
		this.to = to;
	}

	public double convert(double val) {
		Fraction conversion = Measurement.CONVERSIONS.get(this);
		return val * conversion.doubleValue();
	}
	
	public Conversion inverse() {
		return new Conversion(to, from);
	}

	public Measurement getFrom() {
		return from;
	}

	public void setFrom(Measurement from) {
		this.from = from;
	}

	public Measurement getTo() {
		return to;
	}

	public void setTo(Measurement to) {
		this.to = to;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Conversion)) {
			return false;
		}
		Conversion other = (Conversion) obj;
		if (from != other.from) {
			return false;
		}
		if (to != other.to) {
			return false;
		}
		return true;
	}
}
