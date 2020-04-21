package ru.progwards.java2.lessons.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class FindUnused {
    private static List<CObject> unused = new ArrayList<>();

    public static List<CObject> find(List<CObject> roots, List<CObject> objects){
        unused.clear();
        Queue<CObject> queue = new ArrayDeque<>();
        queue.addAll(roots);
        while (!queue.isEmpty()){
            CObject cObject = queue.poll();
            if (cObject.mark == 0) { // if unused
                unused.add(cObject);
                cObject.mark = 1; // make visited
            }else if (cObject.mark == 2){ // if used
                queue.addAll(cObject.references);
                cObject.mark = 1; // make visited
            }
        }
        return unused;
    }
}
