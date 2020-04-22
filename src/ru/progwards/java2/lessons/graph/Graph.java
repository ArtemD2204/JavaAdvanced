package ru.progwards.java2.lessons.graph;

import java.util.ArrayList;
import java.util.List;

class Graph<N, E> {
    List<Node<N, E>> nodes = new ArrayList<>();
    List<Edge<N, E>> edges = new ArrayList<>();
}
