package ru.progwards.java2.lessons.synchro.app.service;

import ru.progwards.java2.lessons.synchro.app.StoreFile;
import ru.progwards.java2.lessons.synchro.app.model.Account;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public class FileStoreService implements StoreService {
    @Override
    public Account get(String id) {
        return null;
    }

    @Override
    public Collection<Account> get() {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public void insert(Account account) {

    }

    @Override
    public void update(Account account) {

    }

    public static void main(String[] args) throws IOException {
        StoreFile.initStoreFile();

    }
}
