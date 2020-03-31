package ru.progwards.java2.lessons.gc;

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
