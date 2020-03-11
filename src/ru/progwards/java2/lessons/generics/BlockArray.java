package ru.progwards.java2.lessons.generics;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BlockArray<T> {

    private T[] arr = (T[]) new Object[2];
    private int size = 0;

    public void add(T elem) {
        if(size >= arr.length) {
            arr = Arrays.copyOf(arr, size * 2);
        }
        arr[size++] = elem;
    }

    public void insert(int pos, T elem) {
        if(size >= arr.length) {
            arr = Arrays.copyOf(arr, size * 2);
        }
        for(int i = size-1; i >= pos; i--) {
            arr[i+1] = arr[i];
        }
        arr[pos] = elem;
        size++;
    }

    public void remove(int pos) {
        for(int i = pos; i < size-1; i++) {
            arr[i] = arr[i+1];
        }
        arr[size-1] = null;
        size--;
    }

    public T get(int pos) {
        if(pos >= size) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return arr[pos];
    }

    public int size() {
        return size;
    }

    public static void main(String[] args) {
        BlockArray<Integer> blockArray = new BlockArray<>();
        for(int i = 0; i < 64; i++) {
            blockArray.add(i);
        }
        System.out.println(Arrays.toString(blockArray.arr));
        System.out.println(blockArray.size);

        blockArray.insert(3, 3000);
        System.out.println(Arrays.toString(blockArray.arr));
        System.out.println(blockArray.size);

        blockArray.remove(3);
        System.out.println(Arrays.toString(blockArray.arr));
        System.out.println(blockArray.size);

        System.out.println(blockArray.get(6));
//        System.out.println(blockArray.arr.length); // Почему здесь Exception ?
    }
}
