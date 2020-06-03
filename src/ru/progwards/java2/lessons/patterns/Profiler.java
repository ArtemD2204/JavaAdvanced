package ru.progwards.java2.lessons.patterns;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum Profiler {
    INSTANCE();

    private ConcurrentHashMap<String, Deque<Long>> sectionStartTimes;
    private ConcurrentHashMap<String, Deque<String>> sectionNames;
    private ConcurrentHashMap<String, LinkedHashMap<String, StatisticInfo>> sections;

    private Profiler() {
        sectionStartTimes = new ConcurrentHashMap<>();
        sectionNames = new ConcurrentHashMap<>();
        sections = new ConcurrentHashMap<>();
    }

    public void enterSection(String name) {
        String threadName = Thread.currentThread().getName();
        LinkedHashMap<String, StatisticInfo> sectionMap = sections.get(threadName);
        Deque<Long> sectionStartTimeDeque = sectionStartTimes.get(threadName);
        Deque<String> sectionNameDeque = sectionNames.get(threadName);
        if (sectionMap == null) {
            sectionMap = new LinkedHashMap<>();
            sections.put(threadName, sectionMap);
            sectionStartTimeDeque = new ArrayDeque<>();
            sectionStartTimes.put(threadName, sectionStartTimeDeque);
            sectionNameDeque = new ArrayDeque<>();
            sectionNames.put(threadName, sectionNameDeque);
        }

        sectionMap.putIfAbsent(name, new StatisticInfo(name));
        sectionStartTimeDeque.push(Instant.now().toEpochMilli());
        sectionNameDeque.push(name);
    }

    public void exitSection(String name) {
        String threadName = Thread.currentThread().getName();
        LinkedHashMap<String, StatisticInfo> sectionMap = sections.get(threadName);
        Deque<Long> sectionStartTimeDeque = sectionStartTimes.get(threadName);
        Deque<String> sectionNameDeque = sectionNames.get(threadName);

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
        String threadName = Thread.currentThread().getName();
        if (!"main".equals(threadName)) {
            LinkedHashMap<String, StatisticInfo> sectionMap = sections.get(threadName);
            return new ArrayList<StatisticInfo>(sectionMap.values());
        } else {
            ArrayList<StatisticInfo> statisticInfoList = new ArrayList<>();
            for (LinkedHashMap<String, StatisticInfo> sectionMap : sections.values()) {
                statisticInfoList.addAll(sectionMap.values());
            }
            return statisticInfoList;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(Thread.currentThread().getName());
        for(int i = 0; i < 10; i++){
            String threadName = "Поток " + i;
            new Thread(threadName){
                public void run(){
                    for (int j = 0; j < 5; j++) {
                        Profiler.INSTANCE.enterSection(threadName + " " + j);
                        try {
                            sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Profiler.INSTANCE.exitSection(threadName + " " + j);
                    }
                }
            }.start();
        }

        Thread.sleep(10000);

        for (StatisticInfo statisticInfo : INSTANCE.getStatisticInfo()) {
            System.out.println(statisticInfo);
        }
    }
}
