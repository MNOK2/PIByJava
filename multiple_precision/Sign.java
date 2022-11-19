package multiple_precision;

class Sign {
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
