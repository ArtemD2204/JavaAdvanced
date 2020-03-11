package ru.progwards.java2.lessons.generics;

import java.util.ArrayList;

public class Box<T extends Fruit> extends ArrayList<T> implements Comparable<Box<? extends Fruit>> {

    public boolean addFruit(T elem) {
        // Каждая коробка может содержать фрукты только одного типа, либо я блоки, либо апельсины.
        // В случае попытки положить апельсин в коробку с яблоками и наоборот выдать исключение UnsupportedOperationException
        if(size() > 0) {
            T lastFruit = get(size()-1);
            if(!lastFruit.getClass().equals(elem.getClass())) {
                throw new UnsupportedOperationException();
            }
        }
        return add(elem);
    }

    public void moveTo(Box<T> toBox) {
        for(int i = size() - 1; i >= 0; i--) {
            toBox.addFruit(get(i));
            remove(i);
        }
    }

    public float getWeight() {
        float weightOfOneFruit = 0.0f;
        if(size() == 0)
            return 0.0f;
        switch (get(0).getClass().getSimpleName()){
            case "Apple":
                weightOfOneFruit = 1.0f;
                break;
            case "Orange":
                weightOfOneFruit = 1.5f;
                break;
        }
        return weightOfOneFruit * size();
    }

    @Override
    public int compareTo(Box<? extends Fruit> o) {
        return Float.compare(getWeight(), o.getWeight());
    }


    public static void main(String[] args) {
        Box<Fruit> boxApple = new Box<>();
        for(int i = 0; i < 10; i++) {
            boxApple.addFruit(new Apple());
        }
//        boxApple.addFruit(new Orange());
        System.out.println(boxApple.getWeight());

        Box<Fruit> boxOrange = new Box<>();
        for(int i = 0; i < 10; i++) {
            boxOrange.addFruit(new Orange());
        }
        System.out.println(boxOrange.getWeight());

//        boxApple.moveTo(boxOrange);
        Box<Fruit> boxOrange2 = new Box<>();
        for(int i = 0; i < 10; i++) {
            boxOrange2.addFruit(new Orange());
        }
        boxOrange2.moveTo(boxOrange);
        System.out.println(boxOrange.getWeight());
        System.out.println(boxOrange2.getWeight());

        System.out.println(boxApple.compareTo(boxOrange));

//        Box<Apple> boxApple = new Box<>();
//        for(int i = 0; i < 10; i++) {
//            boxApple.addFruit(new Apple());
//        }
//        boxApple.addFruit(new Orange());
//        System.out.println(boxApple.getWeight());
//
//        Box<Orange> boxOrange = new Box<>();
//        for(int i = 0; i < 10; i++) {
//            boxOrange.addFruit(new Orange());
//        }
//        System.out.println(boxOrange.getWeight());
//
//        boxApple.moveTo(boxOrange);
//        Box<Orange> boxOrange2 = new Box<>();
//        for(int i = 0; i < 10; i++) {
//            boxOrange2.addFruit(new Orange());
//        }
//        boxOrange2.moveTo(boxOrange);
//        System.out.println(boxOrange.getWeight());
//        System.out.println(boxOrange2.getWeight());
//
//        System.out.println(boxApple.compareTo(boxOrange));
    }
}
