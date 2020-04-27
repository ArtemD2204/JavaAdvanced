package ru.progwards.java2.lessons.synchro.app;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;

public class StoreFile {
    public static void initStoreFile(String pathName, ReadWriteLock lock) throws IOException {
        lock.writeLock().lock();
        try {
            StringBuilder sb = new StringBuilder();
            String lineSeparator = System.getProperty("line.separator");
            List<String> list = new ArrayList<>();
            for (int i = 3; i < 30; i += 1) {
                String id = Integer.toString(i);
                sb.append(id).append(";");
                sb.append("Account_").append(i).append(";"); // add holder
                sb.append((System.currentTimeMillis() + 365L * 24 * 3600 * 1000)).append(";"); // add date
                sb.append(Math.floor(Math.random() * 100_000_000) / 100).append(";"); // add amount
                sb.append(1000 + i); // add pin
                sb.append(createStringWithSpaces(100));
                sb.append(lineSeparator);
                list.add(sb.toString());
                sb.delete(0, sb.length());
            }
            list.sort(Comparator.naturalOrder());
            Path path = Paths.get(pathName);
            if (Files.exists(path))
                Files.delete(path);
            Files.createFile(path);
            for (String str : list) {
                Files.writeString(path, str, StandardOpenOption.APPEND);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static String createStringWithSpaces(int numberOfSpaces) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < numberOfSpaces; j++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
