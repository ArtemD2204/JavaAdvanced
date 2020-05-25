package ru.progwards.java2.lessons.patterns;

import ru.progwards.java1.lessons.bigints.AbsInteger;

public enum AbstractFactory {
    INSTANCE;

    public AbsInteger createInteger(Number number) {
        if (number instanceof Byte) {
            return ByteIntegerFactory.INSTANCE.createInteger(number);
        } else if (number instanceof Short) {
            return ShortIntegerFactory.INSTANCE.createInteger(number);
        } else if (number instanceof Integer) {
            Integer num = (Integer) number;
            if (num >= Byte.MIN_VALUE && num <= Byte.MAX_VALUE) {
                return ByteIntegerFactory.INSTANCE.createInteger(number);
            } else if (num >= Short.MIN_VALUE && num <= Short.MAX_VALUE) {
                return ShortIntegerFactory.INSTANCE.createInteger(number);
            } else {
                return IntIntegerFactory.INSTANCE.createInteger(number);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(AbstractFactory.INSTANCE.createInteger(127).getClass());
        System.out.println(AbstractFactory.INSTANCE.createInteger(32767).getClass());
        System.out.println(AbstractFactory.INSTANCE.createInteger(32768).getClass());
    }
}
