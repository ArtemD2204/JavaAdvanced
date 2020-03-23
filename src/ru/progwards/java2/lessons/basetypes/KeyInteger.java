package ru.progwards.java2.lessons.basetypes;

public class KeyInteger implements HashValue {

    private int key;

    KeyInteger(int key) {
        this.key = key;
    }


    @Override
    public int getHash() {
        return key;
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
