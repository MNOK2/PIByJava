package multiple_precision;

import common.Common;

public class MultiFloat implements Comparable<MultiFloat> {
    public final static int FRACTION_SIZE = 1 << 10;

    private final Sign _sign;
    private final int _exponent;
    private final Digit[] _fraction;

    public MultiFloat(double value) {
        this._sign = value < 0.0 ? Sign.negative() : Sign.positive();
        this._exponent = Common.exponent(value);
        this._fraction = new Digit[FRACTION_SIZE];
        double fraction = Common.fraction(value);
        for (int i = FRACTION_SIZE - 1; i >= 0; i--) {
            this._fraction[i] = new Digit((int)fraction);
            fraction = (fraction - (int)fraction) * 10.0;
        }
    }

    private MultiFloat(Sign sign, int exponent, Digit[] fraction) {
        if (fraction.length != FRACTION_SIZE) throw new IllegalArgumentException();
        this._sign = sign;
        this._exponent = exponent;
        this._fraction = fraction.clone();
    }

    public static MultiFloat zero() {
        Digit[] fraction = new Digit[FRACTION_SIZE];
        for (int i = 0; i < FRACTION_SIZE; i++) fraction[i] = Digit.zero();
        return new MultiFloat(Sign.positive(), 0, fraction);
    }

    public boolean isZero() {
        for (int i = 0; i < FRACTION_SIZE; i++) if (this._fraction[i].compareTo(Digit.zero()) != 0) return false;
        return true;
    }

    @Override
    public int compareTo(MultiFloat other) {
        if (this.isZero() && other.isZero()) return 0;
        if (this._sign.equals(Sign.negative()) && other._sign.equals(Sign.positive())) return -1;
        if (this._sign.equals(Sign.positive()) && other._sign.equals(Sign.negative())) return 1;
        if (this._sign.equals(Sign.negative())) return other.abs().compareTo(this.abs());
        if (this._exponent < other._exponent) return -1;
        if (this._exponent > other._exponent) return 1;
        for (int i = FRACTION_SIZE - 1; i >= 0; i--) {
            int compareResult = this._fraction[i].compareTo(other._fraction[i]);
            if (compareResult != 0) return compareResult;
        }
        return 0;
    }

    public String toString(int digitCount) {
        if (digitCount < 0 || digitCount > FRACTION_SIZE - 1) throw new IllegalArgumentException();

        if (this.isZero()) return "0";
        String result = String.format("%s%s. ", this._sign, this._fraction[FRACTION_SIZE - 1]);
        for (int i = FRACTION_SIZE - 2; i >= FRACTION_SIZE - 1 - digitCount; i--) {
            result += this._fraction[i];
            if (i > FRACTION_SIZE - 1 - digitCount) result += " ";
        }
        result += String.format(" e %s", this._exponent);
        return result;
    }

    public MultiFloat abs() {
        return new MultiFloat(Sign.positive(), this._exponent, this._fraction);
    }
}
