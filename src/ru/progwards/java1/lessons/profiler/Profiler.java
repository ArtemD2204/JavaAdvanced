package ru.progwards.java1.lessons.profiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

public class Profiler {

    private static Deque<Long> sectionStartTimeDeque = new ArrayDeque<>();
    private static Deque<String> sectionNameDeque = new ArrayDeque<>();
    private static LinkedHashMap<String, StatisticInfo> sectionMap = new LinkedHashMap<>();

    public static void enterSection(String name) {
        sectionMap.putIfAbsent(name, new StatisticInfo(name));
        sectionStartTimeDeque.push(Instant.now().toEpochMilli());
        sectionNameDeque.push(name);
    }

    public static void exitSection(String name) {
        int period = (int)(Instant.now().toEpochMilli() - sectionStartTimeDeque.pop());
        sectionMap.get(name).count++;
        sectionMap.get(name).fullTime += period;
        sectionMap.get(name).selfTime += period;
        sectionNameDeque.pop();
        if(!sectionNameDeque.isEmpty()) {
            String externalSectionName = sectionNameDeque.peek();
            sectionMap.get(externalSectionName).selfTime -= period;
        }
    }

    public static List<StatisticInfo> getStatisticInfo() {
        return new ArrayList<StatisticInfo>(sectionMap.values());
    }

    public static void printStatisticInfo(String fileName) throws IOException {
        List<StatisticInfo> list = getStatisticInfo();
        List<String> strList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (StatisticInfo info : list) {
            sb.append("name=").append(info.sectionName).append(" | fulltime=").append(info.fullTime)
                    .append(" | selftime=").append(info.selfTime).append(" | count=").append(info.count);
            strList.add(sb.toString());
            sb.delete(0, sb.length());
        }
        Path path = Paths.get(fileName);
        Files.write(path, strList);
    }
}
