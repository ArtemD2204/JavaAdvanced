package ru.progwards.java2.lessons.basetypes;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoubleHashTable<K extends HashValue, V> {

    class TableItem<K, V> {

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
            return key+":"+item.toString();
        }

    }

    Object[] table;
    private int size;

    public int getTableLength() {
        return table.length;
    }

    DoubleHashTable() {
        table = new Object[101];
        size = 0;
    }

    public void add(K key, V item) {
        int index = key.getHash();
        int step = key.getHashForStep();
        while (true){ //&& !((TableItem<K, V>) table[index]).isRemoved) {
            if (index >= table.length) {
//                throw new ArrayIndexOutOfBoundsException();
                expandTable();
            }
            if(table[index] == null || ((TableItem<K, V>) table[index]).isRemoved)
                break;
            index += step;
        }
        table[index] = new TableItem<K, V>(key, item);
        size++;
    }

    private void expandTable(){
        Object[] oldTable = Arrays.copyOf(table, table.length);
        table = new Object[PrimeNumber.getNearestPrime(table.length * 2)];
        size = 0;
        for(Object o : oldTable) {
            if(o != null && !((TableItem<K, V>) o).isRemoved) {
                TableItem<K, V> item = (TableItem<K, V>) o;
                add(item.getKey(), item.getItem());
            }
        }
    }

    public V get(K key) {
        int index = key.getHash();
        int step = key.getHashForStep();
        while (table[index] != null && !key.equals(((TableItem<K, V>)table[index]).getKey())) {
            if (index >= table.length)
                return null;
            index += step;
        }
        if (table[index] == null || ((TableItem<K, V>)table[index]).isRemoved)
            return null;
        return ((TableItem<K, V>)table[index]).getItem();
    }

    public void remove(K key) {
        int index = key.getHash();
        int step = key.getHashForStep();
        while (table[index] != null && !key.equals(((TableItem<K, V>)table[index]).getKey())) {
            if (index >= table.length)
                return;
            index += step;
        }
        if (table[index] != null && !((TableItem<K, V>)table[index]).isRemoved) {
            ((TableItem<K, V>) table[index]).isRemoved = true;
            size--;
        }
    }

    public void change(K key1, K key2) { // изменить значение ключа у элемента с key1 на key2
        V itemValue = get(key1);
        remove(key1);
        add(key2, itemValue);
    }

    public int size() {
        return size;
    }

    class HashTableIterator implements Iterator<TableItem<K,V>> {

        private int currentTableIndex;
        private int number;

        HashTableIterator() {
            currentTableIndex = 0;
            while(table[currentTableIndex] == null || ((TableItem<K, V>)table[currentTableIndex]).isRemoved) {
                currentTableIndex++;
            }
            number = 0;
        }

        @Override
        public boolean hasNext() {
            return number < size();
        }

        @Override
        public TableItem<K, V> next() {
            if (!hasNext()) throw new NoSuchElementException();
            TableItem<K, V> tableItemToReturn = (TableItem<K, V>) table[currentTableIndex];
            currentTableIndex++;
            while(table[currentTableIndex] == null || ((TableItem<K, V>)table[currentTableIndex]).isRemoved) {
                currentTableIndex++;
            }
            number++;
            return tableItemToReturn;
        }
    }

    public Iterator<TableItem<K,V>> getIterator(){
        return new HashTableIterator();
    }

    public static void main(String[] args) {
        DoubleHashTable<KeyInteger, Integer> intHashTable = new DoubleHashTable<>();
        for(int i = 0; i < 112; i++) {
            intHashTable.add(new KeyInteger(i, intHashTable.getTableLength()), i);
        }
        for(int i = 0; i < 112; i++) {
            System.out.println(intHashTable.get(new KeyInteger(i, intHashTable.getTableLength())));
        }
    }
}
