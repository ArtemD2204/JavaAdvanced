package ru.progwards.java2.lessons.synchro;

public class Philosopher {
    String name;
    Fork right; // вилка справа
    Fork left; // вилка слева
    long reflectTime; // время, которое философ размышляет в мс
    long eatTime; // время, которое философ ест в мс
    long reflectSum; // суммарное время, которое философ размышлял в мс
    long eatSum; // суммарное время, которое философ ел в мс

    public Philosopher(String name, long reflectTime, long eatTime) {
        this.name = name;
        this.reflectTime = reflectTime;
        this.eatTime = eatTime;
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

    // размышлять. Выводит "размышляет "+ name на консоль с периодичностью 0.5 сек
    void reflect() throws InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis()-start < reflectTime) {
            System.out.println("размышляет " + name);
            Thread.sleep(500);
        }
    }

    // есть. Выводит "ест "+ name на консоль с периодичностью 0.5 сек
    void eat() throws InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis()-start < eatTime) {
            System.out.println("ест "+ name);
            Thread.sleep(500);
        }
    }

    void philosopherAlgorithm() {
        //
    }
}
