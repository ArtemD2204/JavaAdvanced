package ru.progwards.java2.lessons.graph;

import java.util.List;

public class CObject {
    public List<CObject> references; // ссылки на другие объекты
    int mark;  // пометка для алгоритма
    // 0 - не используется
    // 1 - посещен
    // 2 - используется
}
