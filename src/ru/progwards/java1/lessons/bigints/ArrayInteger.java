package ru.progwards.java1.lessons.bigints;

import java.math.BigInteger;
import java.util.Arrays;

public class ArrayInteger {
    private byte[] digits;
    private int length;

    public ArrayInteger(int n) {
        digits = new byte[n];
        length = 0;
        Arrays.fill(digits, (byte)0);
    }

    public void fromInt(BigInteger value) {
        length = 0;
        Arrays.fill(digits, (byte)0);
        int valueLength = value.toString().length();
        int startWritePoint = 0;
        if(valueLength > digits.length) {
            startWritePoint = valueLength - digits.length;
        }
        for(int i = 0; i < valueLength; i++) {
            BigInteger remainder = value.remainder(BigInteger.TEN);
            if(i >= startWritePoint) {
                digits[length] = remainder.byteValue();
                length++;
            }
            value = value.divide(BigInteger.TEN);
        }
    }

    public BigInteger toInt() {
        BigInteger intVal = BigInteger.ZERO;
        BigInteger order = BigInteger.ONE;
        for(int i = 0; i < length; i++) {
            BigInteger realNumberFromByte = new BigInteger(String.valueOf(digits[i])).multiply(order);
            intVal = realNumberFromByte.add(intVal);
            order = order.multiply(BigInteger.TEN);
        }
        return intVal;
    }

    public boolean add(ArrayInteger num) {
        for (int i = 0; i < num.length; i++) {
            if(i == length) {
                Arrays.fill(digits, (byte)0);
                length = 0;
                return false;
            }
            byte sum = (byte)(digits[i] + num.digits[i]);
            if(sum > 9) {
                if(i == length - 1) {
                    Arrays.fill(digits, (byte)0);
                    length = 0;
                    return false;
                }
                digits[i] = (byte)(sum % 10);
                digits[i + 1] += 1;
            } else {
                digits[i] = sum;
            }
        }
        return  true;
    }
}
