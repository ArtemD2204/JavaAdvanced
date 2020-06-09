package ru.progwards.java2.lessons.sort;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.*;

public class ThreadsSort {
    private static final int MIN_BLOCK_SIZE = 100000;

    public static <T extends Comparable<T>> void sort(T[] a) throws InterruptedException {
        if (a.length <= MIN_BLOCK_SIZE) {
            QuickSort.sort2(a);
            return;
        }
        int numberOfBlocks = a.length % MIN_BLOCK_SIZE == 0 ? a.length / MIN_BLOCK_SIZE : a.length / MIN_BLOCK_SIZE + 1;
        Queue<T[]> queue = new ConcurrentLinkedQueue<>();
        int arrayIndex = 0;
        for (int blockNum = 0; blockNum < numberOfBlocks - 1; blockNum++) {
            T[] block = (T[]) new Comparable[MIN_BLOCK_SIZE];
            for (int i = 0; i < block.length; i++) {
                block[i] = a[arrayIndex++];
            }
            QuickSort.sort2(block);
            queue.add(block);
        }
        T[] lastBlock = (T[]) new Comparable[a.length - arrayIndex];
        for (int i = 0; i < lastBlock.length; i++) {
            lastBlock[i] = a[arrayIndex++];
        }
        QuickSort.sort2(lastBlock);
        queue.add(lastBlock);

        while (numberOfBlocks > 1) {
            int numberOfThreads = numberOfBlocks / 2;
            CountDownLatch latch = new CountDownLatch(numberOfThreads);
            for (int i = 0; i < numberOfThreads; i++) {
                numberOfBlocks--;
                new Thread(new MergerRunnable<>(queue, latch)).start();
            }
            latch.await();
        }
        T[] result = queue.poll();
        for (int i = 0; i < a.length; i++) {
            a[i] = result[i];
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Integer[] arr = new Integer[80_000];
        try(Scanner scanner = new Scanner(new FileInputStream("data.txt"))) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = scanner.nextInt();
            }
        }

        try {
            long start = System.currentTimeMillis();
            sort(arr);
            System.out.println("time " + (System.currentTimeMillis() - start));

            int count = 0;
            while (count < arr.length-1) {
                Integer current = arr[count];
                Integer next = arr[count+1];
                if (current > next) {
                    System.out.println("Current number greater than next !!!");
                }
                count++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class MergerRunnable <T extends Comparable<T>> implements Runnable {
        private Queue<T[]> queue;
        private CountDownLatch latch;

        private MergerRunnable(Queue<T[]> queue, CountDownLatch latch) {
            this.queue = queue;
            this.latch = latch;
        }

        @Override
        public void run() {
            T[] arr1 = queue.poll();
            T[] arr2 = queue.poll();
            T[] merged = (T[]) new Comparable[arr1.length + arr2.length];
            int arr1Index = 0;
            int arr2Index = 0;
            int mergedIndex = 0;
            while (arr1Index < arr1.length && arr2Index < arr2.length) {
                if (arr1[arr1Index].compareTo(arr2[arr2Index]) < 0) {
                    merged[mergedIndex++] = arr1[arr1Index++];
                } else {
                    merged[mergedIndex++] = arr2[arr2Index++];
                }
            }
            while (arr1Index < arr1.length) {
                merged[mergedIndex++] = arr1[arr1Index++];
            }
            while (arr2Index < arr2.length) {
                merged[mergedIndex++] = arr2[arr2Index++];
            }
            queue.add(merged);
            latch.countDown();
        }
    }
}
