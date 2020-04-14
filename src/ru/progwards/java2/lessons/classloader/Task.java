package ru.progwards.java2.lessons.classloader;

public interface Task {
    // методы для получения и установки
    // времени создания файла
    public int getModifiedDate();
    public void setModifiedDate(int date);
    // метод для обработки данных
    // и возвращения результата в виде строки
    public String process(byte[] data);
}
