package ru.progwards.java2.lessons.patterns;

import java.util.Objects;

public class StatisticInfo {

    public String sectionName;

    // полное время выполнения секции в миллисекундах
    public int fullTime;

    // чистое время выполнения секции в миллисекундах. Для вложенных секций, из времени выполнения внешней секции нужно
    // вычесть времена выполнения вложенных секций.
    public int selfTime;

    public int count;

    StatisticInfo(String sectionName) {
        this.sectionName = sectionName;
        fullTime = 0;
        selfTime = 0;
        count = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticInfo that = (StatisticInfo) o;
        return sectionName.equals(that.sectionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionName);
    }
}
