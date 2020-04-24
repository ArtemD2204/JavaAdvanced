package ru.progwards.java2.lessons.synchro.app.service;

import ru.progwards.java2.lessons.synchro.app.StoreFile;
import ru.progwards.java2.lessons.synchro.app.model.Account;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public class FileStoreService extends RandomAccessFile implements StoreService {
    private long start = 0;
    private long end;

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

    private boolean findAccountStringByID(String id) throws IOException {
        start = 0;
        end = length();
        while(start < end) {
            // ищем середину и движемся к началу строки
            long current = startStringPosition((end + start) / 2);
            seek(current);
            String currentLine = readLine();
            if (currentLine == null)
                return false;
            currentLine = new String(currentLine.getBytes("ISO-8859-1"), "UTF-8");
            // сравниваем заданный id и найденный в файле
            String currentID = currentLine.split(";")[0];
            int compareResult = id.compareTo(currentID);
            if (compareResult == 0) {
                seek(current);
                return true;
            }
            else if (compareResult > 0) {
                start = this.getFilePointer();
            } else {
                end = current;
            }
        }
        seek(startStringPosition(start));
        return false;
    }

    @Override
    public Account get(String id) {
        findAccountStringByID(id);
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
        Path path = Paths.get("StoreFile.csv");
        try(FileStoreService service = new FileStoreService("StoreFile.csv")){
            String str = service.readLine();
            Boolean idFound = service.findAccountStringByID("1");
            if (idFound)
                service.writeBytes(str);
            else{
                String lineSeparator = System.getProperty("line.separator");
                str = str + lineSeparator + service.readLine() + lineSeparator;
                service.seek(service.startStringPosition(service.start));
                service.writeBytes(str);

            }

        }
    }
}
