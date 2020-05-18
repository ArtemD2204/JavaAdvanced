package ru.progwards.java2.lessons.synchro;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Fork {
    boolean isFree;
    Lock lock;

    public Fork() {
        this.isFree = true;
        this.lock = new ReentrantLock();
    }
}
