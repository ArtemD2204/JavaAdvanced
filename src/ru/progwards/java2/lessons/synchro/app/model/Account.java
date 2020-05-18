package ru.progwards.java2.lessons.synchro.app.model;


import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//POJO
public class Account {

    private String id;
    private String holder;
    private Date date;
    private double amount;
    private int pin;
    private Lock lock = new ReentrantLock();

    public Lock getLock() {
        return lock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", holder='" + holder + '\'' +
                ", date=" + date +
                ", amount=" + amount +
                ", pin=" + pin +
                '}';
    }
}