package ru.progwards.java2.lessons.synchro.app.service;

import ru.progwards.java2.lessons.synchro.app.model.Account;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public class ConcurrentAccountService implements AccountService {
    private StoreService service;
    private ReadWriteLock readWriteLock;

    public ConcurrentAccountService(StoreService service, ReadWriteLock readWriteLock){
        this.service = service;
        this.readWriteLock = readWriteLock;
    }

    @Override
    public double balance(Account account) {
        return account.getAmount();
    }

    @Override
    public void deposit(Account account, double amount) {
        double sum = account.getAmount() + amount;
        account.setAmount(sum);
        readWriteLock.writeLock().lock();
        service.update(account);
        readWriteLock.writeLock().unlock();
    }

    @Override
    public void withdraw(Account account, double amount) {

        double sum = account.getAmount() - amount;
        if(sum < 0){
            throw new RuntimeException("Not enough money");
        }
        account.setAmount(sum);
        service.update(account);
    }

    @Override
    public void transfer(Account from, Account to, double amount) {

        double fromSum = from.getAmount() - amount;
        double toSum = to.getAmount() + amount;
        if(fromSum < 0 ){
            throw new RuntimeException("Not enough money");
        }
        from.setAmount(fromSum);
        service.update(from);
        to.setAmount(toSum);
        service.update(to);

    }
}
