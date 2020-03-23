package ru.progwards.java2.lessons.basetypes;

public class KeyString implements HashValue {

    private String key;

    KeyString(String key) {
        this.key = key;
    }

    private long RSHash (String str) {
        long b = 378551;
        long a = 63689;
        long hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = unsignedInt(hash * a + str.charAt(i));
            a = unsignedInt(a * b);
        }
        return hash;
    }

    private static long unsignedInt(long num) {
        return num % Integer.MAX_VALUE;
    }

    @Override
    public int getHash() {
        return (int)RSHash(key);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof KeyString)) {
            return false;
        }
        KeyString that = (KeyString) o;
        return this.key.equals(that.key);
    }
}
