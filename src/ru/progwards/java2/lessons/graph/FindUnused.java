package ru.progwards.java2.lessons.graph;

import java.util.*;

public class FindUnused {

    public static List<CObject> find(List<CObject> roots, List<CObject> objects){
        objects.forEach(o -> o.mark = 0); // make all unused
        Deque<CObject> stack = new ArrayDeque<>(roots);
        while (!stack.isEmpty()){
            CObject cObject = stack.pollFirst();
            if (cObject.mark != 1) { // if unvisited
                cObject.mark = 1; // make visited
                cObject.references.forEach(stack::offerFirst);
            }
        }
        List<CObject> unused = new ArrayList<>();
        for (CObject cObject : objects){
            if (cObject.mark == 0)
                unused.add(cObject);
        }
        return unused;
    }
}
