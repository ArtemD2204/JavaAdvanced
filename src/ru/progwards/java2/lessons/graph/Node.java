package ru.progwards.java2.lessons.graph;

import java.util.ArrayList;
import java.util.List;

class Node<N, E> {
    N info; // информация об узле
    List<Edge<N, E>> in = new ArrayList<>(); // массив входящих ребер
    List<Edge<N, E>> out = new ArrayList<>(); // массив исходящих ребер
}
