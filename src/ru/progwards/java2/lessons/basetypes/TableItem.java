package ru.progwards.java2.lessons.basetypes;

public class TableItem<K, V> {
    private V item;
    private K key;
    boolean isRemoved;

    TableItem(K key, V item) {
        this.key = key;
        this.item = item;
        isRemoved = false;
    }

    K getKey() {
        return key;
    }

    V getItem() {
        return item;
    }

    public String toString() {
        return key.toString()+" : "+item.toString();
    }
}
