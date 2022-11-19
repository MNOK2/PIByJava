package multiple_precision;

class DigitProductRemainderPair {
    private final Digit _product;
    private final Number _remainder;

    public DigitProductRemainderPair(Digit product, Number remainder) {
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