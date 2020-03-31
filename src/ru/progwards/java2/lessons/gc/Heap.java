package ru.progwards.java2.lessons.gc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Heap {

    private int heapSize;
    private byte[] bytes;
    private List<MemoryBlock> freeBlocks;
    private List<MemoryBlock> usedBlocks;

    public Heap(int maxHeapSize) {
        heapSize = maxHeapSize;
        bytes = new byte[maxHeapSize];
        freeBlocks = new LinkedList<>();
        freeBlocks.add(new MemoryBlock(0, maxHeapSize));
        usedBlocks = new LinkedList<>();
    }

    public int malloc(int size) throws OutOfMemoryException {
        int ptr = allocateForOneBlock(size);
        if (ptr == -1) {
            compact();
            ptr = allocateForOneBlock(size);
            if (ptr == -1) {
                throw new OutOfMemoryException();
            }
        }
        return ptr;
    }

    private int allocateForOneBlock(int size) {
        MemoryBlock suitableBlock = freeBlocks.get(0);
        ListIterator<MemoryBlock> listIterator = freeBlocks.listIterator();
        MemoryBlock block;
        while (listIterator.hasNext()) {
            block = listIterator.next();
            if(block.size == size) {
                addBlockToList(block, usedBlocks);
                listIterator.remove();
                return block.pointer;
            }
            if(block.size > size && block.size < suitableBlock.size)
                suitableBlock = block;
        }
        if (suitableBlock.size < size) {
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
    }

    public void free(int ptr) throws InvalidPointerException {
        ListIterator<MemoryBlock> listIterator = usedBlocks.listIterator();
        MemoryBlock block;
        while (listIterator.hasNext()) {
            block = listIterator.next();
            if (block.pointer == ptr) {
                listIterator.remove();
                addBlockToList(block, freeBlocks);
                return;
            }
            if (ptr < block.pointer) {
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
        ListIterator<MemoryBlock> listIterator = usedBlocks.listIterator();
        MemoryBlock prevBlock = listIterator.next();
        MemoryBlock currBlock;
        while (listIterator.hasNext()) {
            currBlock = listIterator.next();
            int diff = currBlock.pointer - (prevBlock.pointer + prevBlock.size);
            if (diff > 0) {
                for(int i = currBlock.pointer; i < (currBlock.pointer + currBlock.size); i++) {
                    bytes[i-diff] = bytes[i];
                }
                currBlock.pointer = prevBlock.pointer + prevBlock.size;
            }
            prevBlock = currBlock;
        }
    }



    public static void main(String[] args) {
        List<Integer> list = new LinkedList<>();
        for(int i = 10; i < 21; i++) {
            list.add(i);
        }

        ListIterator<Integer> listIterator = list.listIterator();
        System.out.println(list);
        System.out.println(listIterator.next());
        System.out.println(listIterator.next());
        System.out.println(listIterator.nextIndex());
        listIterator.add(100);
        System.out.println(list);
        System.out.println(listIterator.nextIndex());
        System.out.println(listIterator.next());
//        System.out.println(listIterator.nextIndex());
        System.out.println("/////////////////");
        System.out.println(listIterator.previousIndex());
        System.out.println(listIterator.previous());
        listIterator.add(101);
        System.out.println(list);
        System.out.println(listIterator.next());
        listIterator.remove();
        System.out.println(list);
        System.out.println(listIterator.next());
    }

}
