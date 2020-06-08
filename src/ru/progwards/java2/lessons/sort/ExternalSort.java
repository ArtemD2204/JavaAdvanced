package ru.progwards.java2.lessons.sort;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class ExternalSort {
    static final int AMOUNT_OF_NUMBERS = 200_000_000;
    static final int MEMORY_SIZE = 10_000;
    static Integer[] memory = new Integer[MEMORY_SIZE];
    static final int START_NUMBER_OF_BLOCKS = (int) Math.ceil(AMOUNT_OF_NUMBERS / (double) MEMORY_SIZE);

    static void sort(String inFileName, String outFileName) {
        String sourceTmpFileName = "tmp_out_0.txt";
        String destTmpFileName = "tmp_out_1.txt";
        long[] blockPointers = readSourceFile(inFileName, sourceTmpFileName);
        if (START_NUMBER_OF_BLOCKS >= MEMORY_SIZE)
            blockPointers = mergePairs(sourceTmpFileName, destTmpFileName, blockPointers);
        /* после попарного слияния блоков (функция mergePairs) остается один из
        // двух временных файлов (sourceTmpFileName или destTmpFileName), содержащий отсортированные блоки
        // сохраняем имя этого файла в переменную sortedBlocksTmpFileName */
        String sortedBlocksTmpFileName = destTmpFileName;
        if (Files.exists(Paths.get(sourceTmpFileName))) {
            sortedBlocksTmpFileName = sourceTmpFileName;
        }
        balancedMultipathMerge(sortedBlocksTmpFileName, outFileName, blockPointers);
    }

    // считывает блоками данные из файла inFileName; отсортированные блоки записывает в tmpFileName;
    // возвращает массив, содержащий указатели на начало блоков в файле tmpFileName
    private static long[] readSourceFile(String inFileName, String tmpFileName) {
        long[] blockPointers = new long[START_NUMBER_OF_BLOCKS];
        try (Scanner scanner = new Scanner(new FileInputStream(inFileName))) {
            Path tmpPath = Paths.get(tmpFileName);
            if (Files.exists(tmpPath))
                Files.delete(tmpPath);
            Files.createFile(tmpPath);
            RandomAccessFile raf = new RandomAccessFile(tmpFileName, "rw");
            String lineSeparator = System.getProperty("line.separator");
            for (int blockNumber = 0; blockNumber < START_NUMBER_OF_BLOCKS; blockNumber++) {
                int blockSize = 0;
                blockPointers[blockNumber] = raf.getFilePointer();
                while (scanner.hasNext() && blockSize < MEMORY_SIZE) {
                    memory[blockSize++] = scanner.nextInt();
                }
                QuickSort.sort3(memory, 0, blockSize - 1);
                for (int k = 0; k < blockSize; k++) {
                    raf.writeBytes(memory[k].toString());
                    raf.writeBytes(lineSeparator);
                }
            }
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blockPointers;
    }

    // сливает отсортированные блоки попарно пока число блоков больше или равно количеству ячеек памяти(длине массива Integer[] memory)
    // конечный результат сохраняется во временный файл
    private static long[] mergePairs(String sourceTmpFileName, String destTmpFileName, long[] blockPointers) {
        try {
            int numberOfBlocks = START_NUMBER_OF_BLOCKS;
            while (numberOfBlocks >= MEMORY_SIZE) {
                RandomAccessFile tmp_source = new RandomAccessFile(sourceTmpFileName, "rw");
                Files.createFile(Paths.get(destTmpFileName));
                RandomAccessFile tmp_dest = new RandomAccessFile(destTmpFileName, "rw");
                int blockCounter = 0;
                for (; blockCounter < numberOfBlocks - 1; blockCounter += 2) {
                    long firstBlockPtrCurrent = blockPointers[blockCounter];
                    long secondBlockPtrCurrent = blockPointers[blockCounter + 1];
                    long endOfFirstBlock = blockPointers[blockCounter + 1];
                    long endOfSecondBlock = blockCounter + 2 < numberOfBlocks ? blockPointers[blockCounter + 2] : tmp_source.length();
                    mergeTwoBlocks(tmp_source, tmp_dest, firstBlockPtrCurrent, secondBlockPtrCurrent, endOfFirstBlock, endOfSecondBlock);
                }
                // если остался последний блок, то копируем его из файла tmp_source в файл tmp_dest
                if (blockCounter == numberOfBlocks - 1) {
                    copyOneBlock(tmp_source, tmp_dest, blockPointers, blockCounter);
                }
                tmp_source.close();
                tmp_dest.close();
                // удаляем временный файл со входными данными
                Files.delete(Paths.get(sourceTmpFileName));
                // меняем местами входной и выходной временные файлы. Временный файл с выходными результатами делаем входным
                String tmp = sourceTmpFileName;
                sourceTmpFileName = destTmpFileName;
                destTmpFileName = tmp;
                // обновляем число блоков numberOfBlocks и указатели на блоки blockPointers
                numberOfBlocks = numberOfBlocks % 2 == 0 ? numberOfBlocks / 2 : numberOfBlocks / 2 + 1;
                blockPointers = updateBlockPointers(blockPointers, numberOfBlocks);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blockPointers;
    }

    private static void mergeTwoBlocks(RandomAccessFile tmp_source, RandomAccessFile tmp_dest,
                                       long firstBlockPtrCurrent, long secondBlockPtrCurrent,
                                       long endOfFirstBlock, long endOfSecondBlock) throws IOException {
        Integer firstNum = readNextInt(tmp_source, firstBlockPtrCurrent);
        long firstBlockPtrNext = tmp_source.getFilePointer();
        Integer secondNum = readNextInt(tmp_source, secondBlockPtrCurrent);
        long secondBlockPtrNext = tmp_source.getFilePointer();
        while (firstBlockPtrCurrent < endOfFirstBlock || secondBlockPtrCurrent < endOfSecondBlock) {
            int blockSize = 0;
            while (blockSize < MEMORY_SIZE && firstBlockPtrCurrent < endOfFirstBlock
                    && secondBlockPtrCurrent < endOfSecondBlock) {
                if (firstNum < secondNum) {
                    memory[blockSize++] = firstNum;
                    firstNum = readNextInt(tmp_source, firstBlockPtrNext);
                    firstBlockPtrCurrent = firstBlockPtrNext;
                    firstBlockPtrNext = tmp_source.getFilePointer();
                } else {
                    memory[blockSize++] = secondNum;
                    secondNum = readNextInt(tmp_source, secondBlockPtrNext);
                    secondBlockPtrCurrent = secondBlockPtrNext;
                    secondBlockPtrNext = tmp_source.getFilePointer();
                }
            }
            while (blockSize < MEMORY_SIZE && firstBlockPtrCurrent < endOfFirstBlock) {
                memory[blockSize++] = firstNum;
                firstNum = readNextInt(tmp_source, firstBlockPtrNext);
                firstBlockPtrCurrent = firstBlockPtrNext;
                firstBlockPtrNext = tmp_source.getFilePointer();
            }
            while (blockSize < MEMORY_SIZE && secondBlockPtrCurrent < endOfSecondBlock) {
                memory[blockSize++] = secondNum;
                secondNum = readNextInt(tmp_source, secondBlockPtrNext);
                secondBlockPtrCurrent = secondBlockPtrNext;
                secondBlockPtrNext = tmp_source.getFilePointer();
            }
            writeDataToFile(tmp_dest, blockSize);
        }
    }

    private static Integer readNextInt(RandomAccessFile raf, long pointer) throws IOException {
        raf.seek(pointer);
        String str = raf.readLine();
        if (str == null) {
            return null;
        }
        return Integer.valueOf(str);
    }

    private static void writeDataToFile(RandomAccessFile tmp_dest, int blockSize) throws IOException {
        String lineSeparator = System.lineSeparator();
        for (int k = 0; k < blockSize; k++) {
            tmp_dest.writeBytes(memory[k].toString());
            tmp_dest.writeBytes(lineSeparator);
        }
    }

    // копировать один блок из файла tmp_source в файл tmp_dest
    private static void copyOneBlock(RandomAccessFile tmp_source, RandomAccessFile tmp_dest,
                                     long[] blockPointers, int blockCounter) throws IOException {
        String lineSeparator = System.lineSeparator();
        long blockPtr = blockPointers[blockCounter];
        long endOfBlock = tmp_source.length();
        while (blockPtr < endOfBlock) {
            Integer num = readNextInt(tmp_source, blockPtr);
            blockPtr = tmp_source.getFilePointer();
            tmp_dest.writeBytes(Integer.toString(num));
            tmp_dest.writeBytes(lineSeparator);
        }
    }

    // обновляем указатели на блоки blockPointers
    private static long[] updateBlockPointers(long[] blockPointers, int numberOfBlocks) {
        long[] updatedBlockPointers = new long[numberOfBlocks];
        for (int i = 0; i < blockPointers.length; i += 2) {
            updatedBlockPointers[i / 2] = blockPointers[i];
        }
        return updatedBlockPointers;
    }

    private static void balancedMultipathMerge(String blocksFileName, String sortedFileName, long[] blockPointers) {
        try (PrintWriter sortedFile = new PrintWriter(new FileOutputStream(new File(sortedFileName)));
             RandomAccessFile blocksFile = new RandomAccessFile(blocksFileName, "rw");) {
            int numberOfBlocks = blockPointers.length; // numberOfBlocks - количество отсортированных блоков в файле blocksFile
            int readBlockSize = MEMORY_SIZE / (numberOfBlocks + 1); // размер блоков, считываемых из файла в memory
            // массив memoryBlockStartIndexes хранит индексы начала блоков, сами блоки хранятся в Integer[] memory;
            // последний элемент массива memoryBlockStartIndexes - это индекс начала блока на результат
            int[] memoryBlockStartIndexes = new int[numberOfBlocks + 1];
            int readBlockStartIndex = 0;
            for (int i = 0; i < numberOfBlocks; i++) {
                memoryBlockStartIndexes[i] = readBlockStartIndex;
                readBlockStartIndex += readBlockSize;
            }
            memoryBlockStartIndexes[numberOfBlocks] = numberOfBlocks * readBlockSize;
            int[] memoryBlockCurrentIndexes = Arrays.copyOf(memoryBlockStartIndexes, memoryBlockStartIndexes.length);
            long[] blockEnds = new long[numberOfBlocks]; // blockEnds - позиции концов блоков в файле
            int memoryIndex = 0;
            for (int i = 0; i < numberOfBlocks; i++) {
                blockEnds[i] = (i == numberOfBlocks - 1) ? blocksFile.length() : blockPointers[i + 1];
                blockPointers[i] = readBlock(blocksFile, blockPointers[i], blockEnds[i], readBlockSize, memoryIndex);
                memoryIndex += readBlockSize;
            }
            int sortedStartIndex = memoryBlockStartIndexes[memoryBlockStartIndexes.length - 1];
            Integer min = Integer.MAX_VALUE;
            while (min != null) {
                // заполняем блок на результат
                for (int sortedIndex = sortedStartIndex; sortedIndex < MEMORY_SIZE; sortedIndex++) {
                    min = findNextMin(blocksFile, blockPointers, blockEnds, numberOfBlocks,
                            memoryBlockCurrentIndexes, memoryBlockStartIndexes, readBlockSize);
                    memory[sortedIndex] = min;
                }
                // записываем блок на результат в файл
                writeToResultFile(sortedFile, sortedStartIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Files.delete(Paths.get(blocksFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // считывает часть блока длиной readBlockSize из файла blocksFile в memory;
    // возвращает указатель на конец считанного блока в файле
    private static long readBlock(RandomAccessFile blocksFile, long blockPointer, long blockEnd,
                                  int readBlockSize, int memoryIndex) throws IOException {
        for (int j = memoryIndex; j < (memoryIndex + readBlockSize); j++) {
            if (blockPointer >= blockEnd) {
                memory[j] = null;
            } else {
                memory[j] = readNextInt(blocksFile, blockPointer);
                blockPointer = blocksFile.getFilePointer();
            }
        }
        return blockPointer;
    }

    private static Integer findNextMin(RandomAccessFile blocksFile, long[] blockPointers, long[] blockEnds, int numberOfBlocks,
                                       int[] memoryBlockCurrentIndexes, int[] memoryBlockStartIndexes, int readBlockSize) throws IOException {
        Integer min = null; // минимальное число
        Integer indexOfBlockWithMinNum = null; // номер блока, содержащего минимальное число
        int i = 0;
        int currentMemoryIndex = 0;
        for (i = 0; i < numberOfBlocks; i++) {
            currentMemoryIndex = memoryBlockCurrentIndexes[i];
            if (currentMemoryIndex >= memoryBlockStartIndexes[i + 1]) {
                memoryBlockCurrentIndexes[i] = memoryBlockStartIndexes[i];
                currentMemoryIndex = memoryBlockCurrentIndexes[i];
                blockPointers[i] = readBlock(blocksFile, blockPointers[i], blockEnds[i], readBlockSize, currentMemoryIndex);
            }
            Integer current = memory[currentMemoryIndex];
            if (current != null) {
                if (min == null || current < min) {
                    min = current;
                    indexOfBlockWithMinNum = i;
                }
            }
        }
        if (indexOfBlockWithMinNum != null)
            memoryBlockCurrentIndexes[indexOfBlockWithMinNum]++;
        return min;
    }

    private static void writeToResultFile(PrintWriter sortedFile, int sortedStartIndex) {
        for (int sortedIndex = sortedStartIndex; sortedIndex < MEMORY_SIZE; sortedIndex++) {
            Integer num = memory[sortedIndex];
            if (num != null)
                sortedFile.println(num);
            else
                break;
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        sort("data.txt", "sorted.txt");
        System.out.println("time: " + (System.currentTimeMillis() - start) / 1000 + " sec");

        try (Scanner scanner = new Scanner(new FileInputStream("sorted.txt"))) {
            int count = 0;
            int curNum = scanner.nextInt();
            count++;
            while (scanner.hasNextInt()) {
                int nextNum = scanner.nextInt();
                if (curNum > nextNum)
                    System.out.println("previous greater than next !!!");
                curNum = nextNum;
                count++;
            }
            System.out.println("count: " + count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
