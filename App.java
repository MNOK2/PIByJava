import java.util.Random;
import multiple_precision.*;

public class App {
    public static void main(String[] args) {
        MultiFloat multiFloat = new MultiFloat(256.512);
        System.out.println(multiFloat.toString(100));

        /*
        testCalculationOnce(6);
        System.out.println();
        testCalculationRepeating(10000, 10);
        */
    }


    private static void testCalculationOnce(int digitsCount) {
        if (digitsCount < 0) throw new IllegalArgumentException();

        MultiInt multiIntA = MultiInt.random(digitsCount);
        MultiInt multiIntB = MultiInt.random(digitsCount);

        System.out.println("a = " + multiIntA);
        System.out.println("b = " + multiIntB);
        System.out.println();
        System.out.println("a + b = " + multiIntA.add(multiIntB));
        System.out.println("a - b = " + multiIntA.sub(multiIntB));
        System.out.println("a * b = " + multiIntA.mul(multiIntB));
        System.out.println("a / b = " + multiIntA.div(multiIntB));
        System.out.println("a % b = " + multiIntA.mod(multiIntB));
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
            MultiInt multiIntA = new MultiInt(intA);
            MultiInt multiIntB = new MultiInt(intB);

            if ((i + 1) % progressInterval == 0) System.out.println(String.format("%d回目の計算中...", i + 1, calculationCount));

            if (multiIntA.add(multiIntB).toInt() != intA + intB) {
                System.out.println("加算に失敗しました。（絶望）");
                return false;
            }
            if (multiIntA.sub(multiIntB).toInt() != intA - intB) {
                System.out.println("減算に失敗しました。（絶望）");
                return false;
            }
            if (multiIntA.mul(multiIntB).toInt() != intA * intB) {
                System.out.println("乗算に失敗しました。（絶望）");
                return false;
            }

            if (multiIntB.isZero()) continue;

            if (multiIntA.div(multiIntB).toInt() != intA / intB) {
                System.out.println("除算に失敗しました。（絶望）");
                return false;
            }
            if (multiIntA.mod(multiIntB).toInt() != intA % intB) {
                System.out.println("剰余算に失敗しました。（絶望）");
                return false;
            }
        }

        long endTime = System.currentTimeMillis();
        
        System.out.println(String.format("正常に終了しました。（計算時間: %.3fs）", (endTime - startTime) / 1000.0f));
        return true;
    }
}
