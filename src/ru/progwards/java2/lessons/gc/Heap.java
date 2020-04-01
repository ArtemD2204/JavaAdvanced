package ru.progwards.java2.lessons.gc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Heap {

    private int heapSize;
    private byte[] bytes;
    List<MemoryBlock> freeBlocks;
    List<MemoryBlock> usedBlocks;

    private List<Integer> pointersWasAlloc = new LinkedList<>();
    private List<Integer> pointersWasFree = new LinkedList<>();

    public Heap(int maxHeapSize) {
        heapSize = maxHeapSize;
        bytes = new byte[maxHeapSize];
        freeBlocks = new LinkedList<>();
        freeBlocks.add(new MemoryBlock(0, maxHeapSize));
        usedBlocks = new LinkedList<>();
    }

    public int malloc(int size) throws OutOfMemoryException {
        int ptr = allocateForOneBlock(size);
//        if (ptr == -1) {
//            System.out.println("\n =====compact() method was called===== \n");
//            compact();
//            ptr = allocateForOneBlock(size);
            if (ptr == -1) {
                throw new OutOfMemoryException();
            }
//        }
        pointersWasAlloc.add(ptr);
        return ptr;
    }

    private int allocateForOneBlock(int size) {
        MemoryBlock suitableBlock = null;
        ListIterator<MemoryBlock> listIterator = freeBlocks.listIterator();
        MemoryBlock block;
        while (listIterator.hasNext()) {
            block = listIterator.next();
            if(block.size == size) {
                addBlockToList(block, usedBlocks);
                listIterator.remove();
                return block.pointer;
            }
            if(block.size > size) {
                if(suitableBlock == null || block.size < suitableBlock.size) {
                    suitableBlock = block;
                }
            }
        }
        if (suitableBlock == null) {
            return -1;
        }
        MemoryBlock used = new MemoryBlock(suitableBlock.pointer, size);
        addBlockToList(used, usedBlocks);
        suitableBlock.pointer = suitableBlock.pointer + size;
        suitableBlock.size = suitableBlock.size - size;

        return used.pointer;
    }

    private void addBlockToList(MemoryBlock blockToAdd, List<MemoryBlock> list) {
        ListIterator<MemoryBlock> listIterator = list.listIterator();
        MemoryBlock block;
        while (listIterator.hasNext()) {
            block = listIterator.next();
            if (block.pointer > blockToAdd.pointer) {
                listIterator.previous();
                listIterator.add(blockToAdd);
                return;
            }
        }
        list.add(blockToAdd);
    }

    public void free(int ptr) throws InvalidPointerException {
//        System.out.println("\n" + "====free() method was called====" + "\n");
        ListIterator<MemoryBlock> listIterator = usedBlocks.listIterator();
        MemoryBlock block;
        while (listIterator.hasNext()) {
            block = listIterator.next();
            if (block.pointer == ptr) {
                addBlockToList(block, freeBlocks);
                listIterator.remove();
                pointersWasFree.add(ptr);
                return;
            }
            if (ptr < block.pointer) {
                System.out.println("pointer:" + ptr);
                System.out.println("Used Blocks:");
                usedBlocks.forEach(x -> System.out.print(x.pointer + ":" + x.size + ":" + (x.pointer + x.size) + "; "));
                System.out.println("");
                pointersWasAlloc.sort(Integer::compare);
                pointersWasFree.sort(Integer::compare);
                System.out.println("Alloc all time: " + pointersWasAlloc);
                System.out.println("Free all time: " + pointersWasFree);
                throw new InvalidPointerException();
            }
        }
    }

    public void defrag() {
        ListIterator<MemoryBlock> listIterator = freeBlocks.listIterator();
        MemoryBlock prevBlock = listIterator.next();
        MemoryBlock currBlock;
        while (listIterator.hasNext()) {
            currBlock = listIterator.next();
            if ((prevBlock.pointer + prevBlock.size) == currBlock.pointer) {
                prevBlock.size += currBlock.size;
                listIterator.remove();
            } else {
                prevBlock = currBlock;
            }
        }
    }

    public void compact() {
        if (usedBlocks.size() == 0) {
            freeBlocks.clear();
            freeBlocks.add(new MemoryBlock(0, heapSize));
            return;
        }
        MemoryBlock firstBlock = usedBlocks.get(0);
        int diff = firstBlock.pointer;
        if (diff > 0) {
            moveUsedBlock(firstBlock, diff);
        }
        if (usedBlocks.size() == 1) {
            addRemainedMemoryToFreeBlocksList();
            return;
        }

        ListIterator<MemoryBlock> listIterator = usedBlocks.listIterator();
        MemoryBlock prevBlock = listIterator.next();
        MemoryBlock currBlock;
        while (listIterator.hasNext()) {
            currBlock = listIterator.next();
            diff = currBlock.pointer - (prevBlock.pointer + prevBlock.size);
            if (diff > 0) {
                moveUsedBlock(currBlock, diff);
            }
            prevBlock = currBlock;
        }
        addRemainedMemoryToFreeBlocksList();
    }

    private void moveUsedBlock(MemoryBlock block, int diff) {
        for(int i = block.pointer; i < (block.pointer + block.size); i++) {
            bytes[i-diff] = bytes[i];
        }
        block.pointer -= diff;
    }

    private void addRemainedMemoryToFreeBlocksList() {
        freeBlocks.clear();
        MemoryBlock lastUsedBlock = usedBlocks.get(usedBlocks.size()-1);
        int ptr = lastUsedBlock.pointer + lastUsedBlock.size;
        freeBlocks.add(new MemoryBlock(ptr, heapSize - ptr));
    }
}
