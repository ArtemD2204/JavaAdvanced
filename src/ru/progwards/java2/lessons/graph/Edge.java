package ru.progwards.java2.lessons.graph;

import java.util.NoSuchElementException;

class Edge<N, E> {
    E info; // информация о ребре
    Node<N, E> out; // вершина, из которой исходит ребро
    Node<N, E> in; // вершина, в которую можно попасть по этому ребру
    double weight; // стоимость перехода
    Node<N, E> getSecond(Node<N, E> first){
        if (first == out)
            return in;
        if (first == in)
            return out;
        throw new NoSuchElementException("node is not incident this edge");
    }

    @Override
    public String toString() {
        return info + " : " + weight;
    }
}
