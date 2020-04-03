package ru.progwards.java2.lessons.gc;

import java.util.*;

public class Heap {

    private int heapSize;
    private byte[] bytes;
    ArrayList<MemoryBlock> freeBlocks;
    TreeMap<Integer, MemoryBlock> usedBlocks;

    public Heap(int maxHeapSize) {
        heapSize = maxHeapSize;
        bytes = new byte[maxHeapSize];
        freeBlocks = new ArrayList<>();
        freeBlocks.add(new MemoryBlock(0, maxHeapSize));
        usedBlocks = new TreeMap<>(Integer::compare);
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
        int indexOfSuitableBlock = binarySearchHighOrEquals(freeBlocks, size);
        if (indexOfSuitableBlock == -1) {
            return -1;
        }

        MemoryBlock suitableBlock = freeBlocks.get(indexOfSuitableBlock);
        if (suitableBlock.size == size) {
            usedBlocks.put(suitableBlock.pointer, suitableBlock);
            freeBlocks.remove(indexOfSuitableBlock);
            return suitableBlock.pointer;
        }

        MemoryBlock used = new MemoryBlock(suitableBlock.pointer, size);
        usedBlocks.put(used.pointer, used);
        suitableBlock.pointer = suitableBlock.pointer + size;
        suitableBlock.size = suitableBlock.size - size;
        if (suitableBlock.size == 0) {
            freeBlocks.remove(indexOfSuitableBlock);
        } else {
            freeBlocks.sort(Comparator.comparingInt(block -> block.size));
        }
        return used.pointer;
    }

    private int binarySearchHighOrEquals(List<MemoryBlock> list, int size) {
        int index = -1;
        int low = 0;
        int high = list.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            MemoryBlock block = list.get(mid);
            if (block.size < size) {
                low = mid + 1;
            } else if (block.size > size) {
                high = mid - 1;
                index = mid;
            } else {
                index = mid;
                break;
            }
        }
        return index;
    }

    public void free(int ptr) throws InvalidPointerException {
        MemoryBlock block = usedBlocks.get(ptr);
        if (block == null) {
            throw new InvalidPointerException();
        }
        freeBlocks.add(block);
        freeBlocks.sort(Comparator.comparingInt(b -> b.size));
        usedBlocks.remove(ptr);
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
        MemoryBlock firstBlock = usedBlocks.firstEntry().getValue();
        int diff = firstBlock.pointer;
        if (diff > 0) {
            moveUsedBlock(firstBlock, diff);
        }
        if (usedBlocks.size() == 1) {
            addRemainedMemoryToFreeBlocksList();
            return;
        }

        MemoryBlock prevBlock = firstBlock;
        for (MemoryBlock currentBlock : usedBlocks.values()) {
            diff = currentBlock.pointer - (prevBlock.pointer + prevBlock.size);
            if (diff > 0) {
                moveUsedBlock(currentBlock, diff);
            }
            prevBlock = currentBlock;
        }
        addRemainedMemoryToFreeBlocksList();
    }

    private void moveUsedBlock(MemoryBlock block, int diff) {
        for (int i = block.pointer; i < (block.pointer + block.size); i++) {
            bytes[i - diff] = bytes[i];
        }
        block.pointer -= diff;
    }

    private void addRemainedMemoryToFreeBlocksList() {
        freeBlocks.clear();
        MemoryBlock lastUsedBlock = usedBlocks.lastEntry().getValue();
        int ptr = lastUsedBlock.pointer + lastUsedBlock.size;
        freeBlocks.add(new MemoryBlock(ptr, heapSize - ptr));
    }

    public void getBytes(int ptr, int size, byte[] bytes) {
        System.arraycopy(this.bytes, ptr, bytes, 0, size);
    }

    public void setBytes(int ptr, int size, byte[] bytes) {
        System.arraycopy(bytes, 0, this.bytes, ptr, size);
    }
}
