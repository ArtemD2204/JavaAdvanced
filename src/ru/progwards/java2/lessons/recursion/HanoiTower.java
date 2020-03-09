package ru.progwards.java2.lessons.recursion;

import java.util.*;

public class HanoiTower {
    private List<LinkedList<Integer>> towers;
    private int size;
    private boolean trace;

    public HanoiTower(int size, int pos) {
        this.size = size;
        this.trace = false;
        towers = new ArrayList<>(List.of(new LinkedList<>(), new LinkedList<>(), new LinkedList<>()));
        LinkedList<Integer> initTower = towers.get(pos);
        for(int i = size; i >= 1; i--) {
            initTower.push(i);
        }
    }

    public void move(int from, int to) {
        movePartOfTower(size, from, to);
    }

    private void movePartOfTower(int towerSize, int from, int to) {
        if(towerSize == 1) {
            towers.get(to).push(towers.get(from).pop());
            if(trace) print();
        } else {
            int tmpPin = 3 - from - to;
            movePartOfTower(towerSize-1, from, tmpPin);
            movePartOfTower(1, from, to);
            movePartOfTower(towerSize-1, tmpPin, to);
        }
    }

    public void print() {
        StringBuilder[] floors = new StringBuilder[size];
        for(int i = 0; i < size; i++) {
            floors[i] = new StringBuilder();
        }
        for(LinkedList<Integer> tower : towers) {
            Iterator<Integer> iterator = tower.descendingIterator();
            // здесь нумерация этажей башни снизу вверх; нижний этаж имеет номер 0; верхний имеет номер size-1
            int floorNumber = size-1;
            while(iterator.hasNext()) {
                int ringSize = iterator.next();
                if(floors[floorNumber].length() == 0) {
                    floors[floorNumber].append("<");
                } else {
                    floors[floorNumber].append(" <");;
                }
                String ringStr = Integer.toString(ringSize);
                if(ringStr.length() == 1){
                    floors[floorNumber].append("00");
                }
                if(ringStr.length() == 2){
                    floors[floorNumber].append("0");
                }
                floors[floorNumber].append(ringSize);
                floors[floorNumber].append(">");
                floorNumber--;
            }
            while(floorNumber >= 0) {
                if(floors[floorNumber].length() == 0) {
                    floors[floorNumber].append("  I  ");
                } else {
                    floors[floorNumber].append("   I  ");;
                }
                floorNumber--;
            }
        }
        for(StringBuilder floor : floors) {
            System.out.println(floor);
        }
        System.out.println("=================");
    }

    public void setTrace(boolean on) {
        this.trace = on;
    }

    public static void main(String[] args) {
        HanoiTower hanoiTower = new HanoiTower(3, 1);
        hanoiTower.print();
        hanoiTower.setTrace(true);
        hanoiTower.move(1, 0);
        hanoiTower.print();
    }
}
