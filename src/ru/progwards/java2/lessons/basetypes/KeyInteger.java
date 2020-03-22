package ru.progwards.java2.lessons.basetypes;

public class KeyInteger implements HashValue {

    private int key;
    private int tableLength;

    KeyInteger(int key, int tableLength) {
        this.key = key;
        this.tableLength = tableLength;
    }


    @Override
    public int getHash() {
        return key % tableLength;
    }

    @Override
    public int getHashForStep() {
        final double A = (Math.pow(5, 0.5) - 1) / 2;
        double d = key * A;
        final int N = 13;
        return (int)(N * (d - Math.floor(d)));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof KeyInteger)) {
            return false;
        }
        KeyInteger that = (KeyInteger) o;
        return this.key == that.key;
    }
}
