package ru.progwards.java1.lessons.bigints;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigAlgebra {
    public static BigDecimal fastPow(BigDecimal num, int pow) {
        if(pow == 0) // базовый случай
            return BigDecimal.ONE;
        if(pow % 2 == 1)
            // нечетная степень: num^pow = num^(pow-1) * num
            return num.multiply(fastPow(num, pow - 1));
        else {
            // четная степень: num^pow = num^(pow/2) * num^(pow/2)
            BigDecimal num2 = fastPow(num, pow / 2);
            return num2.multiply(num2);
        }
    }

    public static BigInteger fibonacci(int n) {
        if(n == 1 || n == 2)
            return BigInteger.ONE;
        BigInteger fib1 = new BigInteger("1");
        BigInteger fib2 = new BigInteger("1");
        for(int i = 3; i <= n; i++) {
            BigInteger oldFib2 = fib2;
            fib2 = fib2.add(fib1);
            fib1 = oldFib2;
        }
        return fib2;
    }
}
