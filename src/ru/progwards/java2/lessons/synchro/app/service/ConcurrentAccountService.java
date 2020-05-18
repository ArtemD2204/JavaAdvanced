package ru.progwards.java2.lessons.synchro.app.service;

import ru.progwards.java2.lessons.synchro.app.model.Account;


public class ConcurrentAccountService implements AccountService {
    private StoreService service;

    public ConcurrentAccountService(StoreService service) {
        this.service = service;
    }

    @Override
    public double balance(Account account) {
        synchronized (account) {
            return account.getAmount();
        }
    }

    @Override
    public void deposit(Account account, double amount) {
        synchronized (account) {
            double sum = account.getAmount() + amount;
            account.setAmount(sum);
            service.update(account);
        }
    }

    @Override
    public void withdraw(Account account, double amount) {
        synchronized (account) {
            double sum = account.getAmount() - amount;
            if (sum < 0) {
                throw new RuntimeException("Not enough money");
            }
            account.setAmount(sum);
            service.update(account);
        }
    }

    @Override
    public void transfer(Account from, Account to, double amount) {
        while (true) {
            if (from.getLock().tryLock() && to.getLock().tryLock()) {
                double fromSum = from.getAmount() - amount;
                double toSum = to.getAmount() + amount;
                if (fromSum < 0) {
                    throw new RuntimeException("Not enough money");
                }
                from.setAmount(fromSum);
                service.update(from);
                to.setAmount(toSum);
                service.update(to);
                break;
            }
        }
    }

}
