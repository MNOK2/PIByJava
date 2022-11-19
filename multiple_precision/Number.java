package multiple_precision;

import java.util.Random;

public class Number implements Comparable<Number> {
    public static final int DIGITS_COUNT_MAX = 1 << 10;

    private final Sign _sign;
    private final Digit[] _digits;

    public Number(int value) {
        int quo = value;
        this._sign = value < 0 ? Sign.negative() : Sign.positive();
        this._digits = new Digit[DIGITS_COUNT_MAX];
        for (int i = 0; i < DIGITS_COUNT_MAX; i++) {
            this._digits[i] = new Digit(Math.abs(quo % 10));
            quo /= 10;
        }
    }

    private Number(Sign sign, Digit[] digits) {
        if (digits.length != DIGITS_COUNT_MAX) throw new IllegalArgumentException();
        this._sign = sign;
        this._digits = digits.clone();
    }

    public static Number zero() {
        Digit[] digits = new Digit[DIGITS_COUNT_MAX];
        for (int i = 0; i < DIGITS_COUNT_MAX; i++) digits[i] = Digit.zero();
        return new Number(Sign.positive(), digits);
    }

    public static Number random(int digitsCount) {
        if (digitsCount < 0 || digitsCount > DIGITS_COUNT_MAX) throw new IllegalArgumentException();
        Digit[] digits = new Digit[DIGITS_COUNT_MAX];
        Random random = new Random();
        for (int i = 0; i < digitsCount; i++) digits[i] = new Digit(random.nextInt(10));
        for (int i = digitsCount; i < DIGITS_COUNT_MAX; i++) digits[i] = Digit.zero();
        return new Number(random.nextFloat() < 0.5f ? Sign.positive() : Sign.negative(), digits);
    }

    public boolean isZero() {
        for (int i = 0; i < DIGITS_COUNT_MAX; i++) if (this._digits[i].compareTo(Digit.zero()) != 0) return false;
        return true;
    }

    @Override
    public int compareTo(Number other) {
        if (this.isZero() && other.isZero()) return 0;
        if (this._sign.equals(Sign.negative()) && other._sign.equals(Sign.positive())) return -1;
        if (this._sign.equals(Sign.positive()) && other._sign.equals(Sign.negative())) return 1;
        for (int i = DIGITS_COUNT_MAX - 1; i >= 0; i--) {
            int compareResult = this._digits[i].compareTo(other._digits[i]);
            if (compareResult != 0) return compareResult;
        }
        return 0;
    }

    public int toInt() {
        int result = 0;
        int weight = 1;
        for (int i = 0; i < DIGITS_COUNT_MAX; i++) {
            result += this._digits[i].toByte() * weight;
            weight *= 10;
        }
        return this._sign.equals(Sign.negative()) ? -result : result;
    }

    @Override
    public String toString() {
        if (this.isZero()) return "0";
        String result = this._sign.toString();
        boolean zeroOngoing = true;
        for (int i = DIGITS_COUNT_MAX - 1; i >= 0; i--) {
            if (this._digits[i].compareTo(Digit.zero()) != 0) zeroOngoing = false;
            if (!zeroOngoing) {
                result += this._digits[i];
                if (i > 0) result += " ";
            }
        }
        return result;
    }

    public Number signReversed() {
        return new Number(this._sign.reversed(), this._digits);
    }

    public Number abs() {
        return new Number(Sign.positive(), this._digits);
    }

    public Number digitShiftedLeft(int count) {
        if (count < 0) return this.digitShiftedRight(-count);
        if (count > DIGITS_COUNT_MAX) return this.digitShiftedLeft(DIGITS_COUNT_MAX);

        Digit[] digits = new Digit[DIGITS_COUNT_MAX];
        for (int i = 0; i < count; i++) digits[i] = Digit.zero();
        for (int i = count; i < DIGITS_COUNT_MAX; i++) digits[i] = this._digits[i - count];
        return new Number(this._sign, digits);
    }

    public Number digitShiftedRight(int count) {
        if (count < 0) return this.digitShiftedLeft(-count);
        if (count > DIGITS_COUNT_MAX) return this.digitShiftedRight(DIGITS_COUNT_MAX);

        Digit[] digits = new Digit[DIGITS_COUNT_MAX];
        for (int i = DIGITS_COUNT_MAX - 1; i >= DIGITS_COUNT_MAX - count; i--) digits[i] = Digit.zero();
        for (int i = DIGITS_COUNT_MAX - count - 1; i >= 0; i--) digits[i] = this._digits[i + count];
        return new Number(this._sign, digits);
    }

    private Number digitInserted(Digit digit) {
        Number result = this.digitShiftedLeft(1);
        result._digits[0] = digit;
        return result;
    }

    public Number add(Number other) {
        if (this._sign.equals(Sign.negative()) && other._sign.equals(Sign.positive())) return other.sub(this.abs());
        if (this._sign.equals(Sign.positive()) && other._sign.equals(Sign.negative())) return this.sub(other.abs());

        Digit[] digits = new Digit[DIGITS_COUNT_MAX];
        int carry = 0;
        for (int i = 0; i < DIGITS_COUNT_MAX; i++) {
            digits[i] = this._digits[i].add(other._digits[i], carry);
            carry = this._digits[i].addCarry(other._digits[i], carry);
        }
        return new Number(this._sign, digits);
    }

    public Number sub(Number other) {
        if (this._sign.equals(Sign.negative()) && other._sign.equals(Sign.positive())) return this.abs().add(other).signReversed();
        if (this._sign.equals(Sign.positive()) && other._sign.equals(Sign.negative())) return this.add(other.abs());
        if (this._sign.equals(Sign.negative())) return other.abs().sub(this.abs());
        if (this.compareTo(other) < 0) return other.sub(this).signReversed();

        Digit[] digits = new Digit[DIGITS_COUNT_MAX];
        int carry = 0;
        for (int i = 0; i < DIGITS_COUNT_MAX; i++) {
            digits[i] = this._digits[i].sub(other._digits[i], carry);
            carry = this._digits[i].subCarry(other._digits[i], carry);
        }
        return new Number(Sign.positive(), digits);
    }

    public Number mul(Number other) {
        if (!this._sign.equals(other._sign)) return this.abs().mul(other.abs()).signReversed();
        if (this._sign.equals(Sign.negative())) return this.abs().mul(other.abs());

        Number result = Number.zero();
        for (int i = 0; i < DIGITS_COUNT_MAX; i++) result = result.add(this.mul(other._digits[i]).digitShiftedLeft(i));
        return result;
    }

    private Number mul(Digit other) {
        if (other.compareTo(Digit.zero()) == 0) return Number.zero();
        if (other.compareTo(new Digit(1)) == 0) return this;

        Digit[] digits = new Digit[DIGITS_COUNT_MAX];
        int carry = 0;
        for (int i = 0; i < DIGITS_COUNT_MAX; i++) {
            digits[i] = this._digits[i].mul(other, carry);
            carry = this._digits[i].mulCarry(other, carry);
        }
        return new Number(this._sign, digits);
    }

    public Number div(Number other) {
        if (other.isZero()) throw new ArithmeticException();
        if (!this._sign.equals(other._sign)) return this.abs().div(other.abs()).signReversed();
        if (this._sign.equals(Sign.negative())) return this.abs().div(other.abs());

        Digit[] digits = new Digit[DIGITS_COUNT_MAX];
        Number remainder = Number.zero();
        boolean zeroOngoing = true;
        for (int i = 0; i < DIGITS_COUNT_MAX; i++) {
            if (zeroOngoing && this._digits[DIGITS_COUNT_MAX - 1 - i].compareTo(Digit.zero()) == 0) {
                digits[DIGITS_COUNT_MAX - 1 - i] = Digit.zero();
                continue;
            }
            zeroOngoing = false;

            remainder = remainder.digitInserted(this._digits[DIGITS_COUNT_MAX - 1 - i]);
            digits[DIGITS_COUNT_MAX - 1 - i] = remainder.divForDigit(other);
            remainder = remainder.modForDigit(other);
        }
        return new Number(Sign.positive(), digits);
    }

    public Number mod(Number other) {
        if (other.isZero()) throw new ArithmeticException();
        if (this._sign.equals(Sign.negative())) return this.abs().mod(other.abs()).signReversed();
        if (other._sign.equals(Sign.negative())) return this.abs().mod(other.abs());

        Number remainder = Number.zero();
        boolean zeroOngoing = true;
        for (int i = 0; i < DIGITS_COUNT_MAX; i++) {
            if (zeroOngoing && this._digits[DIGITS_COUNT_MAX - 1 - i].compareTo(Digit.zero()) == 0) continue;
            zeroOngoing = false;

            remainder = remainder.digitInserted(this._digits[DIGITS_COUNT_MAX - 1 - i]);
            remainder = remainder.modForDigit(other);
        }
        return remainder;
    }

    private Digit divForDigit(Number other) {
        if (!this._sign.equals(other._sign)) throw new IllegalArgumentException();
        if (this._sign.equals(Sign.negative())) return this.abs().divForDigit(other.abs());

        Number remainder = this;
        for (int i = 0; i < 10; i++) {
            if (remainder.compareTo(other) < 0) return new Digit(i);
            remainder = remainder.sub(other);
        }
        throw new IllegalArgumentException();
    }

    private Number modForDigit(Number other) {
        if (!this._sign.equals(other._sign)) throw new IllegalArgumentException();
        if (this._sign.equals(Sign.negative())) return this.abs().modForDigit(other.abs()).signReversed();

        Number remainder = this;
        for (int i = 0; i < 10; i++) {
            if (remainder.compareTo(other) < 0) return remainder;
            remainder = remainder.sub(other);
        }
        throw new IllegalArgumentException();
    }
}
