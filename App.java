import java.util.Random;

public class App {
    public static void main(String[] args) {
        Number a = Number.random(3);
        Number b = Number.random(3);

        System.out.println(a + " + " + b + " = " + a.add(b));
        System.out.println(a + " - " + b + " = " + a.sub(b));
        System.out.println(a + " * " + b + " = " + a.mul(b));
        System.out.println(a + " / " + b + " = " + a.div(b));
        System.out.println(a + " % " + b + " = " + a.mod(b));

        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            int intA = random.nextInt(20000) - 10000;
            int intB = random.nextInt(20000) - 10000;
            a = new Number(intA);
            b = new Number(intB);
            if (a.add(b).toInt() != intA + intB) {
                System.out.println("加算に失敗しました。（絶望）");
                break;
            }
            if (a.sub(b).toInt() != intA - intB) {
                System.out.println("減算に失敗しました。（絶望）");
                break;
            }
            if (a.mul(b).toInt() != intA * intB) {
                System.out.println("乗算に失敗しました。（絶望）");
                break;
            }
            if (a.div(b).toInt() != intA / intB) {
                System.out.println("除算に失敗しました。（絶望）");
                break;
            }
            if (a.mod(b).toInt() != intA % intB) {
                System.out.println("剰余算に失敗しました。（絶望）");
                break;
            }
        }
    }
}
