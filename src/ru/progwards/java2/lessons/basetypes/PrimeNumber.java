package ru.progwards.java2.lessons.basetypes;

public class PrimeNumber {
    public static int getNearestPrime(int num) {
        int nearestPrime = 2;
        int[] arr = new int[num];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = i + 2;
        }
        for(int i = 0; i < arr.length - 1; i++) {
            if(arr[i] == 0)
                continue;
            for(int j = i + 1; j < arr.length; j++) {
                arr[j] = (arr[j] % arr[i] == 0) ? 0 : arr[j];
            }
        }
        for(int i = arr.length - 1; i > 0; i--) {
            if(arr[i] != 0) {
                nearestPrime = arr[i];
                break;
            }
        }
        return nearestPrime;
    }

    public static void main(String[] args) {
        System.out.println(PrimeNumber.getNearestPrime(509*2));
    }
}
