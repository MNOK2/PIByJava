package common;

public class Common {
    public static int exponent(double value) {
        int exponent = 0;
        double temp = value;
        for (; temp >= 10.0; temp /= 10.0) exponent++;
        for (; temp < 1.0; temp *= 10.0) exponent--;
        return exponent;
    }

    public static double fraction(double value) {
        double temp = value;
        for (; temp >= 10.0; temp /= 10.0) ;
        for (; temp < 1.0; temp *= 10.0) ;
        return temp;
    }
}
