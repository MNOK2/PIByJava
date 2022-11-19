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
        Digit.WithCarry digitWithCarry = new Digit.WithCarry(null, 0);
        for (int i = 0; i < DIGITS_COUNT_MAX; i++) {
            digitWithCarry = this._digits[i].add(other._digits[i], digitWithCarry.carry());
            digits[i] = digitWithCarry.digit();
        }
        return new Number(this._sign, digits);
    }

    public Number sub(Number other) {
        if (this._sign.equals(Sign.negative()) && other._sign.equals(Sign.positive())) return this.abs().add(other).signReversed();
        if (this._sign.equals(Sign.positive()) && other._sign.equals(Sign.negative())) return this.add(other.abs());
        if (this._sign.equals(Sign.negative())) return other.abs().sub(this.abs());
        if (this.compareTo(other) < 0) return other.sub(this).signReversed();

        Digit[] digits = new Digit[DIGITS_COUNT_MAX];
        Digit.WithCarry digitWithCarry = new Digit.WithCarry(null, 0);
        for (int i = 0; i < DIGITS_COUNT_MAX; i++) {
            digitWithCarry = this._digits[i].sub(other._digits[i], digitWithCarry.carry());
            digits[i] = digitWithCarry.digit();
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
        Digit.WithCarry digitWithCarry = new Digit.WithCarry(null, 0);
        for (int i = 0; i < DIGITS_COUNT_MAX; i++) {
            digitWithCarry = this._digits[i].mul(other, digitWithCarry.carry());
            digits[i] = digitWithCarry.digit();
        }
        return new Number(this._sign, digits);
    }

    public Number div(Number other) {
        return this.divMod(other).product();
    }

    public Number mod(Number other) {
        return this.divMod(other).remainder();
    }

    public ProductRemainderPair divMod(Number other) {
        if (!this._sign.equals(other._sign)) {
            ProductRemainderPair result = this.abs().divMod(other.abs());
            return new ProductRemainderPair(result.product().signReversed(), this._sign.equals(Sign.negative()) ? result.remainder().signReversed() : result.remainder());
        }
        if (this._sign.equals(Sign.negative())) {
            ProductRemainderPair result = this.abs().divMod(other.abs());
            return new ProductRemainderPair(result.product(), this._sign.equals(Sign.negative()) ? result.remainder().signReversed() : result.remainder());
        }

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
            ProductRemainderPairDigit productRemainderPairDigit = remainder.divModDigit(other);
            digits[DIGITS_COUNT_MAX - 1 - i] = productRemainderPairDigit.product();
            remainder = productRemainderPairDigit.remainder();
        }
        return new ProductRemainderPair(new Number(Sign.positive(), digits), remainder);
    }

    private ProductRemainderPairDigit divModDigit(Number other) {
        if (!this._sign.equals(other._sign)) throw new IllegalArgumentException();
        if (this._sign.equals(Sign.negative())) return this.abs().divModDigit(other.abs());

        Number remainder = this;
        for (int i = 0; i < 10; i++) {
            if (remainder.compareTo(other) < 0) return new ProductRemainderPairDigit(new Digit(i), remainder);
            remainder = remainder.sub(other);
        }
        throw new IllegalArgumentException();
    }

    private static class Sign {
        private final Type _type;
    
        private Sign(Type type) {
            this._type = type;
        }
    
        public static Sign negative() {
            return new Sign(Type.NEGATIVE);
        }
    
        public static Sign positive() {
            return new Sign(Type.POSITIVE);
        }
    
        public Sign reversed() {
            return this.equals(Sign.negative()) ? Sign.positive() : Sign.negative();
        }
    
        public boolean equals(Sign other) {
            return this._type == other._type;
        }
    
        @Override
        public String toString() {
            return this._type == Type.NEGATIVE ? "- " : "+ ";
        }
    
        private static enum Type {
            NEGATIVE,
            POSITIVE
        }
    }

    private static class Digit implements Comparable<Digit> {
        private final byte _value;
    
        public Digit(int value) {
            if (value < 0 || value > 9) throw new IllegalArgumentException();
            this._value = (byte)value;
        }
    
        public static Digit zero() {
            return new Digit(0);
        }

        @Override
        public int compareTo(Digit other) {
            if (this._value < other._value) return -1;
            if (this._value > other._value) return 1;
            return 0;
        }

        public byte toByte() {
            return this._value;
        }
    
        @Override
        public String toString() {
            return Byte.toString(this._value);
        }
    
        public WithCarry add(Digit other, int carry) {
            int sum = this._value + other._value + carry;
            int rem = sum % 10;
            return new WithCarry(new Digit(rem < 0 ? rem + 10 : rem), (sum < 0 ? sum - 9 : sum) / 10);
        }
    
        public WithCarry sub(Digit other, int carry) {
            int diff = this._value - other._value + carry;
            int rem = diff % 10;
            return new WithCarry(new Digit(rem < 0 ? rem + 10 : rem), (diff < 0 ? diff - 9 : diff) / 10);
        }
    
        public WithCarry mul(Digit other, int carry) {
            int pro = this._value * other._value + carry;
            int rem = pro % 10;
            return new WithCarry(new Digit(rem < 0 ? rem + 10 : rem), (pro < 0 ? pro - 9 : pro) / 10);
        }
    
        public static class WithCarry {
            private final Digit _digit;
            private final int _carry;
    
            public WithCarry(Digit digit, int carry) {
                this._digit = digit;
                this._carry = carry;
            }
    
            public Digit digit() {
                return this._digit;
            }
    
            public int carry() {
                return this._carry;
            }
        }
    }

    public static class ProductRemainderPair {
        private final Number _product;
        private final Number _remainder;

        public ProductRemainderPair(Number product, Number remainder) {
            this._product = product;
            this._remainder = remainder;
        }

        public Number product() {
            return this._product;
        }

        public Number remainder() {
            return this._remainder;
        }
    }

    private static class ProductRemainderPairDigit {
        private final Digit _product;
        private final Number _remainder;

        public ProductRemainderPairDigit(Digit product, Number remainder) {
            this._product = product;
            this._remainder = remainder;
        }

        public Digit product() {
            return this._product;
        }

        public Number remainder() {
            return this._remainder;
        }
    }
}
