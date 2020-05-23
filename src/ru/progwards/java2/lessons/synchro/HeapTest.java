
package ru.progwards.java2.lessons.synchro;

//import com.google.inject.internal.cglib.core.$CollectionUtils;

import ru.progwards.java2.lessons.gc.InvalidPointerException;
import ru.progwards.java2.lessons.gc.MemoryBlock;
import ru.progwards.java2.lessons.gc.OutOfMemoryException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class HeapTest extends Thread {
    static final int maxSize = 1000000000;
    static final int maxSmall = 10;
    static final int maxMedium = 100;
    static final int maxBig = 1000;
    static final int maxHuge = 10000;
    int allocated = 0;

    Heap heap;

    HeapTest(Heap heap) {
        this.heap = heap;
    }

    class Block {
        public int ptr;
        public int size;

        public Block(int ptr, int size) {
            this.ptr = ptr;
            this.size = size;
        }

        @Override
        public String toString() {
            return "ptr:" + ptr/* + ", size:" + size*/;
        }
    }

    int getRandomSize() {
        int n = Math.abs(ThreadLocalRandom.current().nextInt() % 10);
        int size = Math.abs(ThreadLocalRandom.current().nextInt());
        if (n < 6)
            size %= maxSmall;
        else if (n < 8)
            size %= maxMedium;
        else if (n < 9)
            size %= maxBig;
        else
            size %= maxHuge;
        if (size > maxSize - allocated)
            size = maxSize - allocated;
        return size;
    }

    void testHeap() throws OutOfMemoryException {

        List<Block> blocks = new ArrayList<>();
        int count = 0;
        int allocTime = 0;
        int freeTime = 0;

        long start = System.currentTimeMillis();
        // alloc and free 30% random
        while ((maxSize - allocated) > maxSize / 100000) {
//            printRes(heap);
//            System.out.println(Thread.currentThread().getName() + blocks);
            long lstart, lstop;
            int size = getRandomSize();

            if (size < 1) {
                continue;
            }

            allocated += size;
            count++;
            lstart = System.currentTimeMillis();
            int ptr = heap.malloc(size);
            lstop = System.currentTimeMillis();
            allocTime += lstop - lstart;
            blocks.add(new Block(ptr, size));
            int n = Math.abs(ThreadLocalRandom.current().nextInt() % 5);
            if (n == 0) {
                n = Math.abs(ThreadLocalRandom.current().nextInt() % blocks.size());
                Block block = blocks.get(n);
                lstart = System.currentTimeMillis();
                try {
                    heap.free(block.ptr);
                } catch (InvalidPointerException e) {

                    //////////////////////
                    System.out.println(Thread.currentThread().getName() + " " + block);
                    e.printStackTrace();
                    //////////////////////

                }
                lstop = System.currentTimeMillis();
                freeTime += lstop - lstart;
                allocated -= block.size;
                blocks.remove(n);
            }
            n = Math.abs(ThreadLocalRandom.current().nextInt() % 100000);
            if (n == 0)
                System.out.println(maxSize - allocated);

        }
        heap.stopBackgroundCompact();
        long stop = System.currentTimeMillis();
        System.out.println(maxSize - allocated);
        System.out.println("malloc time: " + allocTime + " free time: " + freeTime);
        System.out.println("total time: " + (stop - start) + " count: " + count);
//        printRes(heap);
    }

    @Override
    public void run() {
        super.run();
        try {
            testHeap();
        } catch (OutOfMemoryException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int THREADS_NUMBER = 2;
        Heap heap = new Heap(maxSize, false);
        for (int i = 0; i < THREADS_NUMBER; i++) {
            new HeapTest(heap).start();
        }
    }

//    private static class ThreadHeapTest extends Thread {
//        Heap heap;
//        ThreadHeapTest(Heap heap) {
//            this.heap = heap;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//            try {
//                testHeap(heap);
//            } catch (OutOfMemoryException | InvalidPointerException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private void printRes(Heap heap) {
        int freeMemory = 0;
        for (MemoryBlock block : heap.freeBlocks) {
            freeMemory += block.size;
        }
        int freeMemoryDiff = (maxSize - allocated) - freeMemory;
        System.out.println("free memory:" + " " + (maxSize - allocated) + " - " + freeMemory + " = " + freeMemoryDiff);
        int allocMemory = 0;
        for (MemoryBlock block : heap.usedBlocks.values()) {
            allocMemory += block.size;
        }
        int allocMemoryDiff = allocated - allocMemory;
        System.out.println("allocated memory:" + " " + allocated + " - " + allocMemory + " = " + allocMemoryDiff);
        if (freeMemoryDiff != 0) {
            throw new RuntimeException("Free memory error!");
        }
        if (allocMemoryDiff != 0) {
            throw new RuntimeException("Allocated memory error!");
        }
    }
}
