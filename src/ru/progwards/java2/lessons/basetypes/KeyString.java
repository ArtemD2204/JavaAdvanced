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
            hash = Math.abs(hash);
            a = unsignedInt(a * b);
            a = Math.abs(a);
        }
        return hash;
    }

    private long ROT13Hash (String str) {

        long hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = unsignedInt(hash + str.charAt(i));
            hash -= (unsignedInt(hash << 13) | (hash >> 19));
            hash = hash < 0 ? hash + Integer.MAX_VALUE : hash;
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
    public int getHashForStep() {
        return (int)ROT13Hash(key);
    }

    public static void main(String[] args) {
        KeyString keyString = new KeyString("Александр Зубrjdcrbq");
        System.out.println(keyString.getHash());
        System.out.println(keyString.getHashForStep());
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
