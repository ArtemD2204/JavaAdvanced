package ru.progwards.java2.lessons.synchro.app.service;

import ru.progwards.java2.lessons.synchro.app.StoreFile;
import ru.progwards.java2.lessons.synchro.app.model.Account;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class FileStoreService extends RandomAccessFile implements StoreService {

    public FileStoreService(String pathName)
            throws FileNotFoundException {
        super(pathName, "rw");
    }

    private long startStringPosition(long pos) throws IOException {
        // ищем символ конца строки 0x0D (\r) или 0x0A (\n)
        for (long i = pos; i >= 0; i--) {
            seek(i);
            int byteChar = read();
            if (byteChar == 0x0D || byteChar == 0x0A)
                return i + 1;
        }
        return 0;
    }

    // findAccountStringByID возвращает длину строки, либо -1 если строка не найдна
    // ставит указатель(FilePointer) в начало строки с найденным Account
    private int findAccountStringByID(String id) throws IOException {
        long start = 0;
        long end = length();
        while (start != end) {
            // ищем середину и движемся к началу строки
            long current = startStringPosition((end + start) / 2);
            seek(current);
            String currentLine = readLine();
            if (currentLine == null) {
                return -1;
            }
            currentLine = new String(currentLine.getBytes("ISO-8859-1"), "UTF-8");
            // сравниваем заданный id и найденный в файле
            String currentID = currentLine.split(";")[0];
            int compareResult = id.compareTo(currentID);
            if (compareResult == 0) {
                seek(current);
                return currentLine.length();
            } else if (compareResult > 0) {
                start = this.getFilePointer();
            } else {
                end = current;
            }
        }
        seek(startStringPosition(start));
        return -1;
    }

    private Account parseAccount(String str) {
        String[] strArr = str.trim().split(";");
        Account account = new Account();
        account.setId(strArr[0]);
        account.setHolder(strArr[1]);
        account.setDate(new Date(Long.parseLong(strArr[2])));
        account.setAmount(Double.parseDouble(strArr[3]));
        account.setPin(Integer.parseInt(strArr[4]));
        return account;
    }

    private String castAccountToString(Account account) {
        StringBuilder sb = new StringBuilder();
        sb.append(account.getId()).append(";").append(account.getHolder()).append(";").append(account.getDate().getTime())
                .append(";").append(account.getAmount()).append(";").append(account.getPin());
        return sb.toString();
    }

    @Override
    public Account get(String id) {
        try {
            int strLength = findAccountStringByID(id);
            if (strLength == -1)
                throw new RuntimeException("Account not found by id:" + id);
            return parseAccount(readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Account> get() {
        List<Account> list = new ArrayList<>();
        try {
            seek(0);
            String str = readLine();
            while (str != null) {
                if (!str.startsWith(" "))
                    list.add(parseAccount(str));
                str = readLine();
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            int currentStrLength = findAccountStringByID(id);
            if (currentStrLength == -1) {
                throw new RuntimeException("Account not found by id:" + id);
            }
            long pastePointer = getFilePointer();
            readLine();
            long copyPointer = getFilePointer();
            String copiedStr = readLine();
            while (copiedStr != null){
                copiedStr = copiedStr.trim();
                seek(pastePointer);
                updateStrInFileStore(copiedStr, currentStrLength);
                seek(copyPointer);
                pastePointer = copyPointer;
                currentStrLength = readLine().length();
                copyPointer = getFilePointer();
                copiedStr = readLine();
            }
            seek(pastePointer);
            writeBytes(createStringWithSpaces(200));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Account account) {
        try {
            int currentStrLength = findAccountStringByID(account.getId());
            if (currentStrLength != -1) {
                throw new RuntimeException("Account with id:" + account.getId() + " already exist");
            }
            long startWritePointer = getFilePointer();
            Path currentPath = Paths.get(".");
            Path tmpFile = Files.createTempFile(currentPath,"tail_of_StoreFile_", ".tmp");
            RandomAccessFile tmp = new RandomAccessFile(tmpFile.toString(), "rw");
            copyToTmpFile(tmp);
            seek(startWritePointer);
            tmp.seek(0);
            String strToBeInsert = castAccountToString(account);
            while (strToBeInsert != null){
                updateStrInFileStore(strToBeInsert, currentStrLength);
                startWritePointer = getFilePointer();
                readLine();
                currentStrLength = readLine().length();
                seek(startWritePointer);
                strToBeInsert = tmp.readLine();
            }
            writeBytes(createStringWithSpaces(100));
            writeBytes(System.getProperty("line.separator"));
            tmp.close();
            Files.delete(tmpFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // возвращает список скопированных строк
    private void copyToTmpFile(RandomAccessFile tmp) throws IOException {
        String lineSeparator = System.getProperty("line.separator");
        String movedStr = readLine();
        while (movedStr != null) {
            movedStr = movedStr.trim() + lineSeparator;
            tmp.writeBytes(movedStr);
            movedStr = readLine();
        }
    }

    private String createStringWithSpaces(int numberOfSpaces){
        StringBuilder sb = new StringBuilder();
        for (int j=0; j<numberOfSpaces; j++){
            sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public void update(Account account) {
        try {
            int currentStrLength = findAccountStringByID(account.getId());
            if (currentStrLength == -1) {
                throw new RuntimeException("Account with id:" + account.getId() + " does not exist");
            }
            updateStrInFileStore(castAccountToString(account), currentStrLength);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private void updateStrInFileStore(String newStr, int currentStrLength) throws IOException {
        String spaceStr = createStringWithSpaces(currentStrLength - newStr.length());
        writeBytes(newStr + spaceStr);
    }

    public static void main(String[] args) throws IOException {
        StoreFile.initStoreFile();
        Path path = Paths.get("StoreFile.csv");
        try (FileStoreService service = new FileStoreService("StoreFile.csv")) {
//            service.get().forEach(System.out::println);
//            System.out.println("-----------------");
//            service.delete("1");
//            service.get().forEach(System.out::println);
            Integer i = 1000;
            Account acc = new Account();
            String id = i.toString();
            acc.setId(id);
            acc.setPin(1000 + i);
            acc.setHolder("Account_" + i);
            acc.setDate(new Date(System.currentTimeMillis() + 365L * 24 * 3600 * 1000));
            acc.setAmount(Math.floor(Math.random() * 100_000_000)/100);
//            service.delete("15");
            service.insert(acc);
//            Account account20 = service.get("20");
//            account20.setHolder("Vasili");
//            service.update(account20);
//            service.get().forEach(System.out::println);
        }
    }
}
