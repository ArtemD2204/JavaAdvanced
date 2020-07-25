package ru.progwards.java2.lessons.synchro;

import java.util.concurrent.Semaphore;

public class Philosopher extends Thread {
    String name;
    private Fork right; // вилка справа
    private Fork left; // вилка слева
    long reflectTime; // время, которое философ размышляет в мс
    long eatTime; // время, которое философ ест в мс
    long reflectSum; // суммарное время, которое философ размышлял в мс
    long eatSum; // суммарное время, которое философ ел в мс
    private Semaphore semaphore;

    public Philosopher(String name, long reflectTime, long eatTime, Semaphore semaphore) {
        this.name = name;
        this.reflectTime = reflectTime;
        this.eatTime = eatTime;
        this.semaphore = semaphore;
    }

    public void setRight(Fork right) {
        this.right = right;
    }

    public void setLeft(Fork left) {
        this.left = left;
    }

    private boolean takeRight() { // возвращает true, если удалось взять вилку справа
        if (right.isFree) {
            right.isFree = false;
            return true;
        } else
            return false;
    }

    private boolean takeLeft() { // возвращает true, если удалось взять вилку слева
        if (left.isFree) {
            left.isFree = false;
            return true;
        } else
            return false;
    }

    private boolean takeForks() { // возвращает true, если удалось взять вилку слева и вилку справа
        try {
            left.lock.lock();
            right.lock.lock();
            if (takeLeft()) {
                if (takeRight()) {
                    return true;
                } else {
                    putLeft();
                    return false;
                }
            } else {
                return false;
            }
        } finally {
            left.lock.unlock();
            right.lock.unlock();
        }
    }

    private void putLeft() {
        left.isFree = true;
    }

    private void putRight() {
        right.isFree = true;
    }

    private void putForks() {
        try {
            left.lock.lock();
            right.lock.lock();
            putLeft();
            putRight();
        } finally {
            left.lock.unlock();
            right.lock.unlock();
        }
    }

    // размышлять. Выводит "размышляет "+ name на консоль с периодичностью 0.5 сек
    void reflect() throws InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < reflectTime) {
            System.out.println("размышляет " + name);
            Thread.sleep(10);
        }
        reflectSum += reflectTime;
    }

    // есть. Выводит "ест "+ name на консоль с периодичностью 0.5 сек
    void eat() throws InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < eatTime) {
            System.out.println("ест " + name);
            Thread.sleep(10);
        }
        eatSum += eatTime;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                reflect();

                boolean philosopherIsHungry = true;
                while (philosopherIsHungry) {
                    semaphore.acquire();

                    // v 1.0
//                    try {
//                        left.lock.lock();
//                        right.lock.lock();
//                        eat();
//                        philosopherIsHungry = false;
//                    } finally {
//                        left.lock.unlock();
//                        right.lock.unlock();
//                    }

                    // v 2.0
                    if (takeForks()) {
                        eat();
                        philosopherIsHungry = false;
                        putForks();
                    }

                    semaphore.release();
                }

            } catch (InterruptedException e) {
                interrupt();
            } finally {

            }
        }
    }
}
