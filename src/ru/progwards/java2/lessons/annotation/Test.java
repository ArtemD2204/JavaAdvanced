package ru.progwards.java2.lessons.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface  Test {
//    int priority = 0;
    int priority();
}
