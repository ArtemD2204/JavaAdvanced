package ru.progwards.java1.lessons.bigints;

public class AbsInteger {

    public static AbsInteger add(AbsInteger num1, AbsInteger num2) {
        return num1.addNonStatic(num2);
    }

    public AbsInteger addNonStatic(AbsInteger num) {
        return null;
    }

    Number getNumber() {
        return null;
    }
}
