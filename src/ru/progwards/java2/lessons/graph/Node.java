package ru.progwards.java2.lessons.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

class Node<N, E> {
    N info; // информация об узле
    List<Edge<N, E>> in = new ArrayList<>(); // массив входящих ребер
    List<Edge<N, E>> out = new ArrayList<>(); // массив исходящих ребер

    @Override
    public int hashCode() {
        return Objects.hash(info);
    }
}
