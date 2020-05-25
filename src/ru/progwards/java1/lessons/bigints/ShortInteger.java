package ru.progwards.java1.lessons.bigints;

public class ShortInteger extends AbsInteger {
    private short number;

    public ShortInteger(short number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return Short.toString(number);
    }

    @Override
    Short getNumber() {
        return number;
    }

    @Override
    public AbsInteger addNonStatic(AbsInteger num) {
        int sum = this.getNumber().intValue() + num.getNumber().intValue();

        if(sum >= Byte.MIN_VALUE && sum <= Byte.MAX_VALUE)
            return new ByteInteger((byte)sum);
        if(sum >= Short.MIN_VALUE && sum <= Short.MAX_VALUE)
            return new ShortInteger((short) sum);
        return new IntInteger(sum);
    }
}
