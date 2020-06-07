package ru.progwards.java2.lessons.sort;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class ExternalSort {
    static final int AMOUNT_OF_NUMBERS = 20000 /*200_000_000*/;
    static final int MEMORY_SIZE = 100 /*10_000*/;
    static Integer[] memory = new Integer[MEMORY_SIZE];
    static final int START_NUMBER_OF_BLOCKS = (int) Math.ceil(AMOUNT_OF_NUMBERS / (double) MEMORY_SIZE);

    static void sort(String inFileName, String outFileName) {
//        try(Scanner scanner = new Scanner(new FileInputStream(inFileName));
//            PrintWriter printWriter = new PrintWriter(new FileOutputStream(new File(outFileName)));) {
//            for (int j=0; j<1; j++) {
//                int blockSize = 0;
//                while (scanner.hasNext() && blockSize < MEMORY_SIZE) {
//                    memory[blockSize++] = scanner.nextInt();
//                }
//                QuickSort.sort3(memory, 0, blockSize-1);
//
//                for (int k=0; k<blockSize; k++) {
//                    printWriter.println(memory[k]);
//                }
//            }
//
//        } catch (/*FileNotFoundException |*/ IOException e) {
//            e.printStackTrace();
//        }
//        String lineSeparator = System.getProperty("line.separator");
        String sourceTmpFileName = "tmp_out_0.txt";
        String destTmpFileName = "tmp_out_1.txt";
        long[] blockPointers = readSourceFile(inFileName, sourceTmpFileName);
        if (START_NUMBER_OF_BLOCKS >= MEMORY_SIZE)
            blockPointers = mergePairs(sourceTmpFileName, destTmpFileName, blockPointers);

        System.out.println("blockPointers.length: " + blockPointers.length);

        String fileName = destTmpFileName;
        if(Files.exists(Paths.get(sourceTmpFileName))) {
            fileName = sourceTmpFileName;
        }
        try (Scanner scanner = new Scanner(new FileInputStream(fileName))) {
            int count = 0;
            while (scanner.hasNextInt()) {
                scanner.nextInt();
                count++;
            }
            System.out.println("count: " + count);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            Path path = Paths.get(outFileName);
//            if (Files.exists(path))
//                Files.delete(path);
//            Files.createFile(path);

//            while (raf.getFilePointer() < raf.length()) {
//                System.out.println(raf.readLine());
//            }
//            tmpOut0.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            Files.delete(Paths.get(outFileName));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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
        String lineSeparator = System.getProperty("line.separator");
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

//                    int firstNum = readNextInt(tmp_source, firstBlockPtrCurrent);
//                    long firstBlockPtrNext = tmp_source.getFilePointer();
//                    int secondNum = readNextInt(tmp_source, secondBlockPtrCurrent);
//                    long secondBlockPtrNext = tmp_source.getFilePointer();
//
//                    while (firstBlockPtrCurrent < endOfFirstBlock || secondBlockPtrCurrent < endOfSecondBlock) {
//                        int blockSize = 0;
//                        while (blockSize < MEMORY_SIZE && firstBlockPtrCurrent < endOfFirstBlock && secondBlockPtrCurrent < endOfSecondBlock) {
//                            if (firstNum < secondNum) {
//                                memory[blockSize++] = firstNum;
//                                firstNum = readNextInt(tmp_source, firstBlockPtrNext);
//                                firstBlockPtrCurrent = firstBlockPtrNext;
//                                firstBlockPtrNext = tmp_source.getFilePointer();
//                            } else {
//                                memory[blockSize++] = secondNum;
//                                secondNum = readNextInt(tmp_source, secondBlockPtrNext);
//                                secondBlockPtrCurrent = secondBlockPtrNext;
//                                secondBlockPtrNext = tmp_source.getFilePointer();
//                            }
//                        }
//                        while (blockSize < MEMORY_SIZE && firstBlockPtrCurrent < endOfFirstBlock) {
//                            memory[blockSize++] = firstNum;
//                            firstNum = readNextInt(tmp_source, firstBlockPtrNext);
//                            firstBlockPtrCurrent = firstBlockPtrNext;
//                            firstBlockPtrNext = tmp_source.getFilePointer();
//                        }
//                        while (blockSize < MEMORY_SIZE && secondBlockPtrCurrent < endOfSecondBlock) {
//                            memory[blockSize++] = secondNum;
//                            secondNum = readNextInt(tmp_source, secondBlockPtrNext);
//                            secondBlockPtrCurrent = secondBlockPtrNext;
//                            secondBlockPtrNext = tmp_source.getFilePointer();
//                        }
//                        writeDataToFile(tmp_dest, blockSize);
//                    }
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
        int firstNum = readNextInt(tmp_source, firstBlockPtrCurrent);
        long firstBlockPtrNext = tmp_source.getFilePointer();
        int secondNum = readNextInt(tmp_source, secondBlockPtrCurrent);
        long secondBlockPtrNext = tmp_source.getFilePointer();

        while (firstBlockPtrCurrent < endOfFirstBlock || secondBlockPtrCurrent < endOfSecondBlock) {
            int blockSize = 0;
            while (blockSize < MEMORY_SIZE && firstBlockPtrCurrent < endOfFirstBlock && secondBlockPtrCurrent < endOfSecondBlock) {
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

    private static int readNextInt(RandomAccessFile raf, long pointer) throws IOException {
        raf.seek(pointer);
        return Integer.parseInt(raf.readLine());
    }

//    private static void readNumFromFile() {
//        memory[blockSize++] = secondNum;
//        secondNum = readNextInt(tmp_source, secondBlockPtrNext);
//        secondBlockPtrCurrent = secondBlockPtrNext;
//        secondBlockPtrNext = tmp_source.getFilePointer();
//    }

    private static void writeDataToFile(RandomAccessFile tmp_dest, int blockSize) throws IOException {
        String lineSeparator = System.lineSeparator();
        for (int k = 0; k < blockSize; k++) {
            tmp_dest.writeBytes(memory[k].toString());
            tmp_dest.writeBytes(lineSeparator);
        }
    }

    // копировать один блок из файла tmp_source в файл tmp_dest
    private static void copyOneBlock(RandomAccessFile tmp_source, RandomAccessFile tmp_dest, long[] blockPointers, int blockCounter) throws IOException {
        String lineSeparator = System.lineSeparator();
            long blockPtr = blockPointers[blockCounter];
            long endOfBlock = tmp_source.length();
            while (blockPtr < endOfBlock) {
                int num = readNextInt(tmp_source, blockPtr);
                blockPtr = tmp_source.getFilePointer();
                tmp_dest.writeBytes(Integer.toString(num));
                tmp_dest.writeBytes(lineSeparator);
            }
    }
    // обновляем указатели на блоки blockPointers
    private static long[] updateBlockPointers(long[] blockPointers, int numberOfBlocks) {
        long[] updatedBlockPointers = new long[numberOfBlocks];
        for (int i = 0; i < blockPointers.length; i += 2) {
            updatedBlockPointers[i/2] = blockPointers[i];
        }
        return updatedBlockPointers;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        sort("data1.txt", "sorted1.txt");
        System.out.println("time: " + (System.currentTimeMillis()-start)/1000);
//        System.out.println(START_NUMBER_OF_BLOCKS);
    }
}
