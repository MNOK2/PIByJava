package multiple_precision;

public class ProductRemainderPair {
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
