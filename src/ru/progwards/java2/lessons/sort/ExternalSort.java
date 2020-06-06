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
    static final int START_NUMBER_OF_BLOCKS = (int)Math.ceil(AMOUNT_OF_NUMBERS / (double)MEMORY_SIZE);

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
        try(Scanner scanner = new Scanner(new FileInputStream(inFileName))) {
            Path path = Paths.get(outFileName);
            if (Files.exists(path))
                Files.delete(path);
            Files.createFile(path);
            String lineSeparator = System.getProperty("line.separator");
            RandomAccessFile tmpOut0 = new RandomAccessFile("tmp_out_0.txt", "rw");
            long[] blockPointers = new long[START_NUMBER_OF_BLOCKS];
            for (int blockNumber=0; blockNumber<START_NUMBER_OF_BLOCKS; blockNumber++) {
                int blockSize = 0;
                blockPointers[blockNumber] = tmpOut0.getFilePointer();
                while (scanner.hasNext() && blockSize < MEMORY_SIZE) {
                    memory[blockSize++] = scanner.nextInt();
                }
                QuickSort.sort3(memory, 0, blockSize-1);

                for (int k=0; k<blockSize; k++) {
                    tmpOut0.writeBytes(memory[k].toString());
                    tmpOut0.writeBytes(lineSeparator);
                }
            }

            RandomAccessFile tmpOut1 = new RandomAccessFile("tmp_out_1.txt", "rw");
            long firstBlockPtr = blockPointers[0];
            long secondBlockPtr = blockPointers[1];
            tmpOut0.seek(firstBlockPtr);
            int firstNum = Integer.parseInt(tmpOut0.readLine());
            firstBlockPtr = tmpOut0.getFilePointer();
            tmpOut0.seek(secondBlockPtr);
            int secondNum = Integer.parseInt(tmpOut0.readLine());
            secondBlockPtr = tmpOut0.getFilePointer();
            if (firstNum < secondNum) {
                memory[i] = firstNum;
                firstNum = Integer.parseInt(tmpOut0.readLine());
                firstBlockPtr = tmpOut0.getFilePointer();

            }

//            while (raf.getFilePointer() < raf.length()) {
//                System.out.println(raf.readLine());
//            }
            tmpOut0.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            Files.delete(Paths.get(outFileName));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public static void main(String[] args) {
//        long start = System.currentTimeMillis();
//        sort("data.txt", "sorted.txt");
//        System.out.println((System.currentTimeMillis()-start)/1000);
//        System.out.println(START_NUMBER_OF_BLOCKS);
        System.out.println(5%2);
    }
}
