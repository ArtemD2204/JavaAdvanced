package ru.progwards.java2.lessons.generics;

import java.util.Arrays;

public class ArraySort {
    public static <T extends Comparable<T>>void sort(T[] arr) {
        for(int i = 0; i < arr.length-1; i++) {
            for(int j = i+1; j < arr.length; j++) {
                if(arr[i].compareTo(arr[j]) > 0) {
                    T tmp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = tmp;
                }
            }
        }
    }

    public static void main(String[] args) {
        Integer[] intArr = {4,1024,1,3,34,5,6,64,346,36,98,933,7,7};
        sort(intArr);
        System.out.println(Arrays.toString(intArr));
    }
}
