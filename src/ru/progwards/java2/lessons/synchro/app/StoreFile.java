package ru.progwards.java2.lessons.synchro.app;

import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.UUID;

public class StoreFile {
    public static void initStoreFile() throws IOException {
        StringBuilder sb = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");
        Path path = Paths.get("StoreFile.csv");
        if (Files.exists(path))
            Files.delete(path);
        Files.createFile(path);
        for (int i = 0; i < 10 ; i++) {
            String id = Integer.toString(i);
            sb.append(id).append(";");
            sb.append("Account_").append(i).append(";"); // add holder
            sb.append(new Date(System.currentTimeMillis()+365*24*3600*1000)).append(";"); // add date
            sb.append(Math.random()*1_000_000).append(";"); // add amount
            sb.append(1000+i).append(lineSeparator); // add pin
            Files.writeString(path, sb.toString(), StandardOpenOption.APPEND);
            sb.delete(0, sb.length());
        }
    }
}
