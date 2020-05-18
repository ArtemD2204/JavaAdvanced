package ru.progwards.java2.lessons.trees;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TreeTest {
    static final int ITERATIONS = 100000;
    public static void main(String[] args) throws TreeException, FileNotFoundException {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        AvlTree<Integer, Integer> avlTree = new AvlTree<>();
        ArrayList<Integer> intList = new ArrayList<>();
        for(int i=0; i<ITERATIONS; i++) {
            int key = ThreadLocalRandom.current().nextInt();
            if (!intList.contains(key))
                intList.add(key);
        }
        // put test
        long start = System.currentTimeMillis();
        for(Integer key : intList) {
            map.put(key, key);
        }
        long stop = System.currentTimeMillis();
        System.out.println("TreeMap put: " + (stop-start));
        start = System.currentTimeMillis();
        for(Integer key : intList) {
            avlTree.put(key, key);
        }
        stop = System.currentTimeMillis();
        System.out.println("AvlTree put: " + (stop-start));
        System.out.println("----------------------");;

        // find random test
        start = System.currentTimeMillis();
        for(Integer key : intList) {
            map.get(key);
        }
        stop = System.currentTimeMillis();
        System.out.println("TreeMap random find: " + (stop-start));
        start = System.currentTimeMillis();
        for(Integer key : intList) {
            avlTree.find(key);
        }
        stop = System.currentTimeMillis();
        System.out.println("AvlTree random find: " + (stop-start));
        System.out.println("----------------------");

        // find sorted test
        List<Integer> sortedIntList = new ArrayList<>(intList);
        sortedIntList.sort(Integer::compare);
        start = System.currentTimeMillis();
        for (Integer key : sortedIntList) {
            map.get(key);
        }
        stop = System.currentTimeMillis();
        System.out.println("TreeMap sorted find: " + (stop-start));
        start = System.currentTimeMillis();
        for (Integer key : sortedIntList) {
            avlTree.find(key);
        }
        stop = System.currentTimeMillis();
        System.out.println("AvlTree sorted find: " + (stop-start));
        System.out.println("----------------------");

        // delete test
        start = System.currentTimeMillis();
        for (Integer key : intList) {
            map.remove(key);
        }
        stop = System.currentTimeMillis();
        System.out.println("TreeMap random delete: " + (stop-start));
        start = System.currentTimeMillis();
        for (Integer key : intList) {
            avlTree.delete(key);
        }
        stop = System.currentTimeMillis();
        System.out.println("AvlTree random delete: " + (stop-start));
        System.out.println("----------------------");

        System.out.println("find&delete passed OK");
        avlTree.process(System.out::println);

        // from file wiki.train.tokens
        System.out.println("\nfrom file wiki.train.tokens\n");

        File file = new File("wiki.train.tokens");
        ArrayList<String> tokensList = new ArrayList<>();

        try(Scanner scanner = new Scanner(file)) {
            scanner.useDelimiter("[^a-zA-Z]+");
            while (scanner.hasNext()) {
                String str = scanner.next();
                if (!tokensList.contains(str))
                    tokensList.add(str);
            }
        }
        // put test
        TreeMap<String, String> strMap = new TreeMap<>();
        AvlTree<String, String> strAvlTree = new AvlTree<>();
        start = System.currentTimeMillis();
        for(String str : tokensList) {
            strMap.put(str, str);
        }
        stop = System.currentTimeMillis();
        System.out.println("TreeMap put: " + (stop-start));
        start = System.currentTimeMillis();
        for(String str : tokensList) {
            strAvlTree.put(str, str);
        }
        stop = System.currentTimeMillis();
        System.out.println("AvlTree put: " + (stop-start));
        System.out.println("----------------------");

        // find random test
        start = System.currentTimeMillis();
        for(String str : tokensList) {
            strMap.get(str);
        }
        stop = System.currentTimeMillis();
        System.out.println("TreeMap random find: " + (stop-start));
        start = System.currentTimeMillis();
        for(String str : tokensList) {
            strAvlTree.find(str);
        }
        stop = System.currentTimeMillis();
        System.out.println("AvlTree random find: " + (stop-start));
        System.out.println("----------------------");

        // find sorted test
        ArrayList<String> sortedTokensList = new ArrayList<>(tokensList);
        sortedTokensList.sort(Comparator.naturalOrder());
        start = System.currentTimeMillis();
        for (String str : sortedTokensList) {
            strMap.get(str);
        }
        stop = System.currentTimeMillis();
        System.out.println("TreeMap sorted find: " + (stop-start));
        start = System.currentTimeMillis();
        for (String str : sortedTokensList) {
            strAvlTree.find(str);
        }
        stop = System.currentTimeMillis();
        System.out.println("AvlTree sorted find: " + (stop-start));
        System.out.println("----------------------");
        // delete test
        start = System.currentTimeMillis();
        for (String str : tokensList) {
            strMap.remove(str);
        }
        stop = System.currentTimeMillis();
        System.out.println("TreeMap random delete: " + (stop-start));
        start = System.currentTimeMillis();
        for (String str : tokensList) {
            strAvlTree.delete(str);
        }
        stop = System.currentTimeMillis();
        System.out.println("AvlTree random delete: " + (stop-start));
        System.out.println("----------------------");

        System.out.println("find&delete passed OK");
        avlTree.process(System.out::println);

    }
}
