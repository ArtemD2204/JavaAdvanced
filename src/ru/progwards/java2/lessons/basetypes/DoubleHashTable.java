package ru.progwards.java2.lessons.basetypes;

public class DoubleHashTable<K extends HashValue, V> {

    class TableItem<K, V> {

        private V item;
        private K key;

        TableItem(K key, V item) {
            this.key = key;
            this.item = item;
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

    public int getTableLength() {
        return table.length;
    }

    DoubleHashTable() {
        table = new Object[101];
    }



    public void add(K key, V item) {
        int index = key.getHash();
        int step = key.getHashForStep();
        while (table[index] != null) {
            if (index >= table.length) {
                throw new ArrayIndexOutOfBoundsException();
            }
            index += step;
        }
        table[index] = new TableItem<K, V>(key, item);
    }

    private void expandTable(){};

}
