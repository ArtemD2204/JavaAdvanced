package ru.progwards.java2.lessons.http;

import ru.progwards.java2.lessons.synchro.app.model.Account;
import ru.progwards.java2.lessons.synchro.app.service.AccountService;
import ru.progwards.java2.lessons.synchro.app.service.FileStoreService;
import ru.progwards.java2.lessons.synchro.app.service.StoreService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AtmClient implements AccountService {

    @Override
    public double balance(Account account) {
        try (Socket socket = new Socket("localhost", 40000);
             InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream();
             Scanner scanner = new Scanner(inputStream);
             PrintWriter pr = new PrintWriter(outputStream, true)) {

            String request = "GET /balance?account=" + account.getId() + " HTTP/1.1\n" + "hostname: localhost\n\n";
            pr.println(request);
//            socket.shutdownOutput();

            if (!scanner.hasNextLine())
                throw new RuntimeException("Server error!");
            String responseFirstLine = scanner.nextLine();
            if (!responseFirstLine.split(" ")[1].equals("200"))
                throw new RuntimeException(responseFirstLine);
            while (scanner.hasNextLine() && !scanner.nextLine().isEmpty()) {
            }
            if (scanner.hasNextLine())
                return Double.parseDouble(scanner.nextLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Input data error");
    }

    @Override
    public void deposit(Account account, double amount) {

    }

    @Override
    public void withdraw(Account account, double amount) {

    }

    @Override
    public void transfer(Account from, Account to, double amount) {

    }

    public static void main(String[] args) {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        StoreService storeService = new FileStoreService("StoreFile.csv", lock);
        AtmClient atmClient = new AtmClient();
        System.out.println(atmClient.balance(storeService.get("3")));
    }
}
