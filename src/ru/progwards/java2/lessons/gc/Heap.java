package ru.progwards.java2.lessons.gc;

import java.util.*;

public class Heap {

    private int heapSize;
    private byte[] bytes;
    ArrayList<MemoryBlock> freeBlocks;
    TreeSet<MemoryBlock> usedBlocks;

    public Heap(int maxHeapSize) {
        heapSize = maxHeapSize;
        bytes = new byte[maxHeapSize];
        freeBlocks = new ArrayList<>();
        freeBlocks.add(new MemoryBlock(0, maxHeapSize));
        usedBlocks = new TreeSet<>(Comparator.comparingInt(block -> block.pointer));
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
        return ptr;
    }

    private int allocateForOneBlock(int size) {
        int indexOfSuitableBlock = binarySearchHighOrEquals(freeBlocks, size);
        MemoryBlock suitableBlock = freeBlocks.get(indexOfSuitableBlock);

        if (indexOfSuitableBlock == -1) {
            return -1;
        }

        if (suitableBlock.size == size) {
            usedBlocks.add(suitableBlock);
            freeBlocks.remove(indexOfSuitableBlock);
            return suitableBlock.pointer;
        }

        MemoryBlock used = new MemoryBlock(suitableBlock.pointer, size);
        usedBlocks.add(used);
        suitableBlock.pointer = suitableBlock.pointer + size;
        suitableBlock.size = suitableBlock.size - size;
        if (suitableBlock.size < 1) {
            freeBlocks.remove(indexOfSuitableBlock);
        } else {
            freeBlocks.sort(Comparator.comparingInt(block -> block.size));
        }
        return used.pointer;
    }

    private int binarySearchHighOrEquals(List<MemoryBlock> list, int size) {
        int index = -1;
        int low = 0;
        int high = list.size()-1;
        while (low <= high) {
            int mid = (low + high) / 2;
            MemoryBlock block = list.get(mid);
            if (block.size < size) {
                low = mid + 1;
            } else if (block.size > size) {
                high = mid - 1;
                index = mid;
            } else if (block.size == size) {
                index = mid;
                break;
            }
        }
        return index;
    }

    public void free(int ptr) throws InvalidPointerException {
        MemoryBlock block = usedBlocks.ceiling(new MemoryBlock(ptr, 0));
        if (block != null && block.pointer == ptr) {
            freeBlocks.add(block);
            freeBlocks.sort(Comparator.comparingInt(b -> b.size));
            usedBlocks.remove(block);
            return;
        }
        throw new InvalidPointerException();
    }



//    public void defrag() {
//        ListIterator<MemoryBlock> listIterator = freeBlocks.listIterator();
//        MemoryBlock prevBlock = listIterator.next();
//        MemoryBlock currBlock;
//        while (listIterator.hasNext()) {
//            currBlock = listIterator.next();
//            if ((prevBlock.pointer + prevBlock.size) == currBlock.pointer) {
//                prevBlock.size += currBlock.size;
//                listIterator.remove();
//            } else {
//                prevBlock = currBlock;
//            }
//        }
//    }

    public void compact() {
        if (usedBlocks.size() == 0) {
            freeBlocks.clear();
            freeBlocks.add(new MemoryBlock(0, heapSize));
            return;
        }
        MemoryBlock firstBlock = usedBlocks.first();
        int diff = firstBlock.pointer;
        if (diff > 0) {
            moveUsedBlock(firstBlock, diff);
        }
        if (usedBlocks.size() == 1) {
            addRemainedMemoryToFreeBlocksList();
            return;
        }

        Iterator<MemoryBlock> iterator = usedBlocks.iterator();
        MemoryBlock prevBlock = iterator.next();
        MemoryBlock currBlock;
        while (iterator.hasNext()) {
            currBlock = iterator.next();
            diff = currBlock.pointer - (prevBlock.pointer + prevBlock.size);
            if (diff > 0) {
                moveUsedBlock(currBlock, diff);
            }
            prevBlock = currBlock;
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
        MemoryBlock lastUsedBlock = usedBlocks.last();
        int ptr = lastUsedBlock.pointer + lastUsedBlock.size;
        freeBlocks.add(new MemoryBlock(ptr, heapSize - ptr));
    }
}
