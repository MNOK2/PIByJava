package multiple_precision;

class Digit implements Comparable<Digit> {
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
