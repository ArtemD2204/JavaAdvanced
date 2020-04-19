package ru.progwards.java2.lessons.threads;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrintScan {
    private static Lock printLock = new ReentrantLock();
    private static Lock scanLock = new ReentrantLock();
    static void print(String name, int pages){
        printLock.lock();
        try {
            for (int i = 1; i <= pages; i++) {
                System.out.println("print " + name + " page " + i);
            }
        } finally {
            printLock.unlock();
        }
    }

    static void scan(String name, int pages){
        scanLock.lock();
        try {
            for (int i = 1; i <= pages; i++) {
                System.out.println("scan " + name + " page " + i);
            }
        } finally {
            scanLock.unlock();
        }
    }

    static class PrintThread implements Runnable{
        private String name;
        private int pages;
        PrintThread(String name, int pages) {
            this.name = name;
            this.pages = pages;
        }
        @Override
        public void run() {
            print(name, pages);
        }
    }

    static class ScanThread implements Runnable{
        private String name;
        private int pages;
        ScanThread(String name, int pages) {
            this.name = name;
            this.pages = pages;
        }
        @Override
        public void run() {
            scan(name, pages);
        }
    }

    public static void main(String... args){
        final int PAGES_NUM = 5;
        final int DOC_NUM = 10;
        for (int i=1; i<=DOC_NUM; i++){
            new Thread(new PrintThread("PrintDoc"+i, PAGES_NUM)).start();
            new Thread(new ScanThread("ScanDoc"+i, PAGES_NUM)).start();
        }
    }
}
