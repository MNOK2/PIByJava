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

    public Digit add(Digit other, int carry) {
        int sum = this._value + other._value + carry;
        int rem = sum % 10;
        return new Digit(rem < 0 ? rem + 10 : rem);
    }

    public Digit sub(Digit other, int carry) {
        int diff = this._value - other._value + carry;
        int rem = diff % 10;
        return new Digit(rem < 0 ? rem + 10 : rem);
    }

    public Digit mul(Digit other, int carry) {
        int pro = this._value * other._value + carry;
        int rem = pro % 10;
        return new Digit(rem < 0 ? rem + 10 : rem);
    }

    public int addCarry(Digit other, int carry) {
        int sum = this._value + other._value + carry;
        return (sum < 0 ? sum - 9 : sum) / 10;
    }

    public int subCarry(Digit other, int carry) {
        int diff = this._value - other._value + carry;
        return (diff < 0 ? diff - 9 : diff) / 10;
    }

    public int mulCarry(Digit other, int carry) {
        int pro = this._value * other._value + carry;
        return (pro < 0 ? pro - 9 : pro) / 10;
    }
}
