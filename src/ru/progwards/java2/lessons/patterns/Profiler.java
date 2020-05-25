package ru.progwards.java2.lessons.patterns;

import java.time.Instant;
import java.util.*;

public enum Profiler {
    INSTANCE();

    private Deque<Long> sectionStartTimeDeque;
    private Deque<String> sectionNameDeque;
    private LinkedHashMap<String, StatisticInfo> sectionMap;

    private Profiler() {
        sectionStartTimeDeque = new ArrayDeque<>();
        sectionNameDeque = new ArrayDeque<>();
        sectionMap = new LinkedHashMap<>();
    }

    public void enterSection(String name) {
        sectionMap.putIfAbsent(name, new StatisticInfo(name));
        sectionStartTimeDeque.push(Instant.now().toEpochMilli());
        sectionNameDeque.push(name);
    }

    public void exitSection(String name) {
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

    public List<StatisticInfo> getStatisticInfo() {
        return new ArrayList<StatisticInfo>(sectionMap.values());
    }
}
