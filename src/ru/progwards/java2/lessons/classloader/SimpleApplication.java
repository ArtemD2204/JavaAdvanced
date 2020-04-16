package ru.progwards.java2.lessons.classloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleApplication {
    public static void main(String[] args) throws IOException {
        System.out.println("SimpleApplication: main стартовал");
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10_000_000; i++){
            list.add(i);
        }
        new SimpleApplication().foo("");
    }

    public void foo(String str) throws IOException {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1_000_000; i++){
            list.add(i);
        }
    }
}
