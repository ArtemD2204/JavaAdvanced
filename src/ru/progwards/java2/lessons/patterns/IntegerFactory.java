package ru.progwards.java2.lessons.patterns;

import ru.progwards.java1.lessons.bigints.AbsInteger;

public interface IntegerFactory {
    AbsInteger createInteger(Number number);
}
