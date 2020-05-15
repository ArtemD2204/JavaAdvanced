package ru.progwards.java2.lessons.synchro;

import java.util.concurrent.Semaphore;

public class Philosopher extends Thread {
    String name;
    Fork right; // вилка справа
    Fork left; // вилка слева
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

    public Fork getRight() {
        return right;
    }

    public void setRight(Fork right) {
        this.right = right;
    }

    public Fork getLeft() {
        return left;
    }

    public void setLeft(Fork left) {
        this.left = left;
    }

//    private void takeRightFork() {
//        if (getRight().isFree)
//            getRight().isFree = false;
//    }
//
//    //
//    private void takeLeftFork() {
//        if (getLeft().isFree)
//            getLeft().isFree = false;
//    }

    private boolean takeForks() { // возвращает true, если удалось взять вилку справа и вилку слева
        if (getRight().isFree)
            getRight().isFree = false;
        else
            return false;
        if (getLeft().isFree) {
            getLeft().isFree = false;
            return true;
        } else {
            getRight().isFree = true;
            return false;
        }
    }

    private void putForks() {
        getRight().isFree = true;
        getLeft().isFree = true;
    }

    // размышлять. Выводит "размышляет "+ name на консоль с периодичностью 0.5 сек
    void reflect() throws InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < reflectTime) {
//            System.out.println("размышляет " + name);
            Thread.sleep(500);
        }
        reflectSum += reflectTime;
    }

    // есть. Выводит "ест "+ name на консоль с периодичностью 0.5 сек
    void eat() throws InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < eatTime) {
            System.out.println("ест " + name);
            Thread.sleep(500);
        }
        eatSum += eatTime;
    }

    @Override
    public void run() {
        while (true) {
            try {
                reflect();
                boolean philosopherIsHungry = true;
                while (philosopherIsHungry) {
                    semaphore.acquire();
                    if (takeForks()) {
                        eat();
                        philosopherIsHungry = false;
                        putForks();
                    }
                    semaphore.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
//            } finally {
//                putForks();
//                semaphore.release();
            }
        }
    }
}
