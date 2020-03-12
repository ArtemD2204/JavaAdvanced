package ru.progwards.java2.lessons.calculator;

import java.util.LinkedList;
import java.util.Scanner;

public class Calculator {
    // решение без учета скобок
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

    // решение с учетом скобок
    // Чтобы учесть скобки, делаем рекурсивный вызов функции для выражения в скобках
    // добавляем кусок кода в строки 62 - 74, 80 - 113
    public static int calculateWithParentheses(String expression) {
        LinkedList<Integer> numbers = new LinkedList<>();
        LinkedList<String> operations = new LinkedList<>();
        Scanner scanner = new Scanner(expression);
        scanner.useDelimiter("");
        int num1 = Integer.parseInt(scanner.next());
        numbers.addFirst(num1);
        while(scanner.hasNext()) {
            String symbol = scanner.next();
            // Начало1
            if("(".equals(symbol)) {
                StringBuilder stringBuilder = new StringBuilder();
                symbol = scanner.next();
                while(!(")".equals(symbol))) {
                    stringBuilder.append(symbol);
                    symbol = scanner.next();
                }
                num1 = calculateWithParentheses(stringBuilder.toString());
                numbers.addFirst(num1);
                continue;
            }
            // Конец1
            switch (symbol) {
                case "+":
                case "-":
                    operations.addFirst(symbol);
                    break;
//                    Начало 2
                case "*":
                    num1 = numbers.removeFirst();
                    symbol = scanner.next();
                    if("(".equals(symbol)) {
                        StringBuilder stringBuilder = new StringBuilder();
                        symbol = scanner.next();
                        while(!(")".equals(symbol))) {
                            stringBuilder.append(symbol);
                            symbol = scanner.next();
                        }
                        num1 = num1 * calculateWithParentheses(stringBuilder.toString());
                    } else {
                        num1 = num1 * Integer.parseInt(symbol);
                    }
                    numbers.addFirst(num1);
                    break;
                case "/":
                    num1 = numbers.removeFirst();
                    symbol = scanner.next();
                    if("(".equals(symbol)) {
                        StringBuilder stringBuilder = new StringBuilder();
                        symbol = scanner.next();
                        while(!(")".equals(symbol))) {
                            stringBuilder.append(symbol);
                            symbol = scanner.next();
                        }
                        num1 = num1 / calculateWithParentheses(stringBuilder.toString());
                    } else {
                        num1 = num1 / Integer.parseInt(symbol);
                    }
                    numbers.addFirst(num1);
                    break;
//                    Конец 2
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
        System.out.println(calculateWithParentheses("7+6-(8-2)*2+9*(8+1)-7*8/2/2*(9-8+2+3*2)"));
    }
}
