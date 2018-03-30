package org.jointheleague.ir;

/**
 * This class is used to represent any rational number in the form of an integer
 * divided by another integer.
 */
public class Fraction extends Number {
	private static final long serialVersionUID = -8953535960702943968L;

	private int numerator;
	private int denominator;

	public Fraction(int num, int den) {
		this.numerator = num;
		this.denominator = den;
	}

	public static Fraction parseFraction(String frac) {
		String[] spl = frac.split("/");
		int num = Integer.parseInt(spl[0]);
		int den = Integer.parseInt(spl[1]);

		return new Fraction(num, den);
	}

	public int getNumerator() {
		return numerator;
	}

	public int getDenominator() {
		return denominator;
	}

	public Fraction reciprocal() {
		return new Fraction(denominator, numerator);
	}

	@Override
	public int intValue() {
		return (int) floatValue();
	}

	@Override
	public long longValue() {
		return (long) doubleValue();
	}

	@Override
	public float floatValue() {
		return numerator / (float) denominator;
	}

	@Override
	public double doubleValue() {
		return numerator / (double) denominator;
	}

	@Override
	public String toString() {
		return numerator + "/" + denominator;
	}
}
