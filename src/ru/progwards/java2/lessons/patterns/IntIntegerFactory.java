package ru.progwards.java2.lessons.patterns;

import ru.progwards.java1.lessons.bigints.AbsInteger;
import ru.progwards.java1.lessons.bigints.IntInteger;

public enum IntIntegerFactory implements IntegerFactory {
    INSTANCE;

    @Override
    public AbsInteger createInteger(Number number) {
        return new IntInteger(number.intValue());
    }
}
