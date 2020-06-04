package ru.progwards.java2.lessons.http;

import ru.progwards.java2.lessons.synchro.app.StoreFile;
import ru.progwards.java2.lessons.synchro.app.model.Account;
import ru.progwards.java2.lessons.synchro.app.service.AccountService;
import ru.progwards.java2.lessons.synchro.app.service.ConcurrentAccountService;
import ru.progwards.java2.lessons.synchro.app.service.FileStoreService;
import ru.progwards.java2.lessons.synchro.app.service.StoreService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CashMachineServer {

    public static void main(String[] args) throws IOException {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        StoreFile.initStoreFile("StoreFile.csv", lock);
        StoreService storeService = new FileStoreService("StoreFile.csv", lock);
        AccountService accountService = new ConcurrentAccountService(storeService);
        try (ServerSocket serverSocket = new ServerSocket(40000)) {
            while (true) {
                    Socket incoming = serverSocket.accept();
                    new Thread(new RequestHandler(incoming, accountService, storeService)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class RequestHandler implements Runnable {
        private Socket socket;
        private AccountService accountService;
        private StoreService storeService;

        RequestHandler(Socket socket, AccountService accountService, StoreService storeService) {
            this.socket = socket;
            this.accountService = accountService;
            this.storeService = storeService;
        }

        @Override
        public void run() {
            try (
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream()
            ) {
                try (Scanner scanner = new Scanner(inputStream)) {
                    // формат запроса
                    // GET /resource?param1=value1&param2=value2 HTTP/1.1
                    // hostname: localhost
                    // пустая строка
                    String[] startStr = scanner.nextLine().split(" ");
                    String firstHeader = scanner.nextLine();

//                    socket.shutdownInput();

                    String hostname = firstHeader.split(" ")[1];
                    String URI = startStr[1];
                    String[] methodAndParams = URI.substring(1).split("\\?");
                    String methodName = methodAndParams[0];
                    String[] params = methodAndParams[1].split("&");
                    Map<String, String> parametersMap = new HashMap<>();
                    for (String parameter : params) {
                        String[] parameterArr = parameter.split("=");
                        parametersMap.put(parameterArr[0], parameterArr[1]);
                    }

                    String response = "HTTP/1.1 200 OK\n" + "Content-Type: text/html; charset=utf-8\n";
                    switch (methodName) {
                        case "balance":
                            Account account = storeService.get(parametersMap.get("account"));
                            String balance = Double.toString(accountService.balance(account));
                            response = response + "Content-Length: " + balance.length() + "\n\n" + balance;
                            break;
                        case "deposit":
                            account = storeService.get(parametersMap.get("account"));
                            double amount = Double.parseDouble(parametersMap.get("amount"));
                            accountService.deposit(account, amount);
                            response = response + "Content-Length: 0\n\n";
                            break;
                        case "withdraw":
                            account = storeService.get(parametersMap.get("account"));
                            amount = Double.parseDouble(parametersMap.get("amount"));
                            accountService.withdraw(account, amount);
                            response = response + "Content-Length: 0\n\n";
                            break;
                        case "transfer":
                            Account from = storeService.get(parametersMap.get("from"));
                            Account to = storeService.get(parametersMap.get("to"));
                            amount = Double.parseDouble(parametersMap.get("amount"));
                            accountService.transfer(from, to, amount);
                            response = response + "Content-Length: 0\n\n";
                            break;
                        default:
                            break;
                    }
                    PrintWriter pr = new PrintWriter(outputStream, true);
                    pr.println(response);
                }
//                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
