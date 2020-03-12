package ru.progwards.java2.lessons.calculator;

import java.util.LinkedList;
import java.util.Scanner;

public class Calculator {
    public static int calculate(String expression) {
        LinkedList<Integer> numbers = new LinkedList<>();
        LinkedList<String> operations = new LinkedList<>();
        Scanner scanner = new Scanner(expression);
        scanner.useDelimiter("");
        int num1 = Integer.parseInt(scanner.next());
        numbers.addFirst(num1);
        while(scanner.hasNext()) {
            String symbol = scanner.next();
            switch (symbol) {
                case "+":
                case "-":
                    operations.addFirst(symbol);
                    break;
                case "*":
                    num1 = numbers.removeFirst();
                    num1 = num1 * Integer.parseInt(scanner.next());
                    numbers.addFirst(num1);
                    break;
                case "/":
                    num1 = numbers.removeFirst();
                    num1 = num1 / Integer.parseInt(scanner.next());
                    numbers.addFirst(num1);
                    break;
                default:
                    numbers.addFirst(Integer.parseInt(symbol));
            }
        }
        num1 = numbers.removeLast();
        while(!operations.isEmpty()) {
            String operation = operations.removeLast();
            switch (operation) {
                case "+":
                    num1 = num1 + numbers.removeLast();
                    break;
                case "-":
                    num1 = num1 - numbers.removeLast();
                    break;
            }
        }
        return num1;
    }

    public static void main(String[] args) {
        System.out.println(calculate("2+3*2"));
    }
}
