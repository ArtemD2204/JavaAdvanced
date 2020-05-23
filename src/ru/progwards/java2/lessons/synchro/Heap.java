package ru.progwards.java2.lessons.synchro;

import ru.progwards.java2.lessons.gc.InvalidPointerException;
import ru.progwards.java2.lessons.gc.MemoryBlock;
import ru.progwards.java2.lessons.gc.OutOfMemoryException;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Heap {

    private int heapSize;
    private byte[] bytes;
    CopyOnWriteArrayList<MemoryBlock> freeBlocks;
    ConcurrentSkipListMap<Integer, MemoryBlock> usedBlocks;
    private BackgroundCompactThread backgroundCompact;

    public Heap(int maxHeapSize, boolean runBackgroundCompact) {
        heapSize = maxHeapSize;
        bytes = new byte[maxHeapSize];
        freeBlocks = new CopyOnWriteArrayList<>();
        freeBlocks.add(new MemoryBlock(0, maxHeapSize));
        usedBlocks = new ConcurrentSkipListMap<>(Integer::compare);
        // компактизация в фоновом режиме
        this.backgroundCompact = new BackgroundCompactThread();
        if (runBackgroundCompact)
            backgroundCompact.start();
    }

    public void startBackgroundCompact() {
        if (!backgroundCompact.isAlive())
            backgroundCompact.start();
    }

    public void stopBackgroundCompact() {
        if (backgroundCompact.isAlive())
            backgroundCompact.interrupt();
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
        synchronized (freeBlocks) {
            int indexOfSuitableBlock = binarySearchHighOrEquals(freeBlocks, size);
            if (indexOfSuitableBlock == -1) {
                return -1;
            }

            MemoryBlock suitableBlock = freeBlocks.get(indexOfSuitableBlock);

            int allocBlockPtr = suitableBlock.pointer;
            int allocBlockSize = suitableBlock.size;
            while (usedBlocks.containsKey(allocBlockPtr)) {
                allocBlockPtr++;
                allocBlockSize--;
                if (allocBlockSize < size) {
                    indexOfSuitableBlock++;
                    suitableBlock = freeBlocks.get(indexOfSuitableBlock);
                    allocBlockPtr = suitableBlock.pointer;
                    allocBlockSize = suitableBlock.size;
                }
            }

            if (suitableBlock.size == size) {
                usedBlocks.put(suitableBlock.pointer, suitableBlock);
                freeBlocks.remove(indexOfSuitableBlock);
                return suitableBlock.pointer;
            }

            MemoryBlock allocBlock = new MemoryBlock(allocBlockPtr, size);
            usedBlocks.put(allocBlock.pointer, allocBlock);
            suitableBlock.size = suitableBlock.size - size - (allocBlockPtr - suitableBlock.pointer);
            suitableBlock.pointer = allocBlockPtr + size;
            if (suitableBlock.size == 0) {
                freeBlocks.remove(indexOfSuitableBlock);
            } else {
                freeBlocks.sort(Comparator.comparingInt(block -> block.size));
            }
            return allocBlock.pointer;
        }
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
        synchronized (freeBlocks) {
            freeBlocks.add(block);
            freeBlocks.sort(Comparator.comparingInt(b -> b.size));
        }
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
            synchronized (freeBlocks) {
                freeBlocks.clear();
                freeBlocks.add(new MemoryBlock(0, heapSize));
            }
            return;
        }

        List<MemoryBlock> usedBlocksList = new ArrayList<>(usedBlocks.values());
        usedBlocksList.sort(Comparator.comparingInt(b -> b.pointer));

        MemoryBlock prevBlock = usedBlocksList.get(0);
        int diff = prevBlock.pointer;
        if (diff > 0) {
            moveUsedBlock(prevBlock, diff);
        }
        if (usedBlocks.size() == 1) {
            addRemainedMemoryToFreeBlocksList(prevBlock);
            return;
        }

        for (int i = 1; i < usedBlocksList.size(); i++) {
            MemoryBlock currentBlock = usedBlocksList.get(i);
            diff = currentBlock.pointer - (prevBlock.pointer + prevBlock.size);
            if (diff > 0) {
                moveUsedBlock(currentBlock, diff);
            }
            prevBlock = currentBlock;
        }
        addRemainedMemoryToFreeBlocksList(usedBlocksList.get(usedBlocksList.size()-1));
    }

    private void moveUsedBlock(MemoryBlock block, int diff) {
        synchronized (bytes) {
            for (int i = block.pointer; i < (block.pointer + block.size); i++) {
                bytes[i - diff] = bytes[i];
            }
            block.pointer -= diff;
        }
    }

    private void addRemainedMemoryToFreeBlocksList(MemoryBlock lastUsedBlock) {
        synchronized (freeBlocks) {
            freeBlocks.clear();
            int ptr = lastUsedBlock.pointer + lastUsedBlock.size;
            freeBlocks.add(new MemoryBlock(ptr, heapSize - ptr));
        }
    }

    private class BackgroundCompactThread extends Thread {
        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                compact();
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    interrupt();
                }
            }
        }
    }

//    public void getBytes(int ptr, int size, byte[] bytes) {
//        System.arraycopy(this.bytes, ptr, bytes, 0, size);
//    }
//
//    public void setBytes(int ptr, int size, byte[] bytes) {
//        System.arraycopy(bytes, 0, this.bytes, ptr, size);
//    }
}
