package ru.progwards.java2.lessons.recursion;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GoodsWithLambda {
    private List<Goods> goodsList;

    void setGoods(List<Goods> list) {
        goodsList = new ArrayList<>(list);
    }

    List<Goods> sortByName() {
        List<Goods> sortedByName = new ArrayList<>(goodsList);
        sortedByName.sort(Comparator.comparing(good -> good.name));
        return sortedByName;
    }

    List<Goods> sortByNumber() {
        List<Goods> sortedByNumber = new ArrayList<>(goodsList);
        sortedByNumber.sort(Comparator.comparing(g -> g.number.toLowerCase()));
        return sortedByNumber;
    }

    List<Goods> sortByPartNumber() {
        List<Goods> sortedByNumber = new ArrayList<>(goodsList);
        sortedByNumber.sort(Comparator.comparing(g -> g.number.substring(0, 3).toLowerCase()));
        return sortedByNumber;
    }

    List<Goods> sortByAvailabilityAndNumber() {
        List<Goods> sortedByNumber = new ArrayList<>(goodsList);
        sortedByNumber.sort((g1, g2) -> {
            if(g1.available == g2.available) {
                return g1.number.compareToIgnoreCase(g2.number);
            } else {
                return Integer.compare(g1.available, g2.available);
            }
        });
        return sortedByNumber;
    }

    List<Goods> expiredAfter(Instant date) {
        return goodsList.stream().takeWhile(goods -> goods.expired.getEpochSecond() <= date.getEpochSecond())
                .sorted(Comparator.comparingLong(goods -> goods.expired.getEpochSecond())).collect(Collectors.toList());
    }

    List<Goods> countLess(int count) {
        return goodsList.stream().takeWhile(goods -> goods.available < count).collect(Collectors.toList());
    }

    List<Goods> countBetween(int count1, int count2) {
        Predicate<Goods> predicate1 = goods -> goods.available > count1;
        Predicate<Goods> predicate2 = goods -> goods.available < count2;
        return goodsList.stream().takeWhile(predicate1.and(predicate2)).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        GoodsWithLambda goodsWithLambda = new GoodsWithLambda();
        goodsWithLambda.setGoods(List.of(
                new Goods("Мягкая игрушка", "ммм", 20, 567.89, Instant.now()),
                new Goods("Книга", "ККК", 10, 500.55, Instant.now()),
                new Goods("Велосипед", "ввв", 20, 8500.11, Instant.now()),
                new Goods("Автомобиль", "ААА", 1, 1000500.55, Instant.now())
        ));
        System.out.println(goodsWithLambda.sortByAvailabilityAndNumber());
        System.out.println(goodsWithLambda.countBetween(1, 30));
    }
}
