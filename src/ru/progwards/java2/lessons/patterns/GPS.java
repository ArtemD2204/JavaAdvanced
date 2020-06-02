package ru.progwards.java2.lessons.patterns;

public class GPS {
    public double lat; // широта
    public double lon; // долгота
    public long time; // время в мс

    @Override
    public String toString() {
        return "lat:" + lat + "; lon:" + lon + "; time:" + time;
    }
}
