package ru.progwards.java2.lessons.threads;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Summator {
    private int count;
    private BigInteger sum;
    private Lock lock;

    Summator(int count){
        this.count = count;
        sum = BigInteger.ZERO;
        lock = new ReentrantLock();
    }

    private void addToSum(BigInteger num){
        lock.lock();
        try {
            sum = sum.add(num);
        } finally {
            lock.unlock();
        }
    }
    public BigInteger sum(BigInteger number) throws InterruptedException {
        BigInteger i = BigInteger.ZERO;
        BigInteger numOfThreads = new BigInteger(Integer.toString(count));
        BigInteger segment = number.divide(numOfThreads);
        BigInteger beforeLastNum = numOfThreads.subtract(BigInteger.ONE);
        List<Thread> threads = new ArrayList<>();
        // threads from 1 to numOfThreads-1
        while (i.compareTo(beforeLastNum) < 0){
            BigInteger from = i.multiply(segment).add(BigInteger.ONE);
            BigInteger to = i.add(BigInteger.ONE).multiply(segment);
            threads.add(new Thread(new SumThread(this, from, to)));
            i = i.add(BigInteger.ONE);
        }
        // the last Thread
        BigInteger from = beforeLastNum.multiply(segment).add(BigInteger.ONE);
        threads.add(new Thread(new SumThread(this, from, number)));
        for (Thread thread : threads){
            thread.start();
            thread.join();
        }
        return sum;
    }

    static class SumThread implements Runnable{
        private Summator summator;
        private BigInteger from;
        private BigInteger to;
        SumThread(Summator summator, BigInteger from, BigInteger to) {
            this.summator = summator;
            this.from = from;
            this.to = to;
        }
        @Override
        public void run() {
            BigInteger sum = BigInteger.ZERO;
            while (from.compareTo(to) < 1){
                sum = sum.add(from);
                from = from.add(BigInteger.ONE);
            }
            summator.addToSum(sum);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i=1; i<=13; i++) {
            Summator summator = new Summator(i);
            System.out.println("Number of threads " + i + " : " + summator.sum(new BigInteger("1000")));
        }
    }
}
