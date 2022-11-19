import java.util.Random;
import multiple_precision.Number;

public class App {
    public static void main(String[] args) {
        testCalculationOnce(6);
        System.out.println();
        testCalculationRepeating(10000, 10);
    }


    private static void testCalculationOnce(int digitsCount) {
        if (digitsCount < 0) throw new IllegalArgumentException();

        Number numberA = Number.random(digitsCount);
        Number numberB = Number.random(digitsCount);

        System.out.println("a = " + numberA);
        System.out.println("b = " + numberB);
        System.out.println();
        System.out.println("a + b = " + numberA.add(numberB));
        System.out.println("a - b = " + numberA.sub(numberB));
        System.out.println("a * b = " + numberA.mul(numberB));
        System.out.println("a / b = " + numberA.div(numberB));
        System.out.println("a % b = " + numberA.mod(numberB));
    }

    private static boolean testCalculationRepeating(int calculationCount, int progressPrintCount) {
        if (calculationCount < 0) throw new IllegalArgumentException();
        if (calculationCount < progressPrintCount) throw new IllegalArgumentException();

        final int CALCULATION_VALUE_ORIGIN = -100000;
        final int CALCULATION_VALUE_RANGE = 200000;

        Random random = new Random();
        long startTime = System.currentTimeMillis();

        int progressInterval = calculationCount / progressPrintCount;
        System.out.println(String.format("=== 連続計算を開始: %d回 ===", calculationCount));
        for (int i = 0; i < calculationCount; i++) {
            int intA = random.nextInt(CALCULATION_VALUE_RANGE) + CALCULATION_VALUE_ORIGIN;
            int intB = random.nextInt(CALCULATION_VALUE_RANGE) + CALCULATION_VALUE_ORIGIN;
            Number numberA = new Number(intA);
            Number numberB = new Number(intB);

            if ((i + 1) % progressInterval == 0) System.out.println(String.format("%d回目の計算中...", i + 1, calculationCount));

            if (numberA.add(numberB).toInt() != intA + intB) {
                System.out.println("加算に失敗しました。（絶望）");
                return false;
            }
            if (numberA.sub(numberB).toInt() != intA - intB) {
                System.out.println("減算に失敗しました。（絶望）");
                return false;
            }
            if (numberA.mul(numberB).toInt() != intA * intB) {
                System.out.println("乗算に失敗しました。（絶望）");
                return false;
            }

            if (numberB.isZero()) continue;

            if (numberA.div(numberB).toInt() != intA / intB) {
                System.out.println("除算に失敗しました。（絶望）");
                return false;
            }
            if (numberA.mod(numberB).toInt() != intA % intB) {
                System.out.println("剰余算に失敗しました。（絶望）");
                return false;
            }
        }

        long endTime = System.currentTimeMillis();
        
        System.out.println(String.format("正常に終了しました。（計算時間: %.3fs）", (endTime - startTime) / 1000.0f));
        return true;
    }
}
