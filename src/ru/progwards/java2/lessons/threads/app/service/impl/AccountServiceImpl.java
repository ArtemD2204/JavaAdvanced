package ru.progwards.java2.lessons.threads.app.service.impl;

import ru.progwards.java2.lessons.tests.app.model.Account;
import ru.progwards.java2.lessons.tests.app.service.AccountService;
import ru.progwards.java2.lessons.tests.app.service.StoreService;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AccountServiceImpl implements AccountService {


    private StoreService service;
    private Lock lock = new ReentrantLock();

    public AccountServiceImpl(){

    }

    public AccountServiceImpl(StoreService service){
        this.service = service;
    }

    @Override
    public double balance(Account account) {
        lock.lock();
        try {
            return account.getAmount();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deposit(Account account, double amount) {
        lock.lock();
        try {
            double sum = account.getAmount() + amount;
            account.setAmount(sum);
            service.update(account);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void withdraw(Account account, double amount) {
        lock.lock();
        try {
            double sum = account.getAmount() - amount;
            if (sum < 0) {
                throw new RuntimeException("Not enough money");
            }
            account.setAmount(sum);
            service.update(account);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void transfer(Account from, Account to, double amount) {
        lock.lock();
        try {
            double fromSum = from.getAmount() - amount;
            double toSum = to.getAmount() + amount;
            if (fromSum < 0) {
                throw new RuntimeException("Not enough money");
            }
            from.setAmount(fromSum);
            service.update(from);
            to.setAmount(toSum);
            service.update(to);
        } finally {
            lock.unlock();
        }
    }

}
