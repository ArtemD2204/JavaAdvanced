package ru.progwards.java2.lessons.gc;

public class MemoryBlock {
    int pointer; // "указатель" - индекс первой ячейки в массиве, размещенного блока
    int size;

    MemoryBlock(int pointer, int length) {
        this.pointer = pointer;
        this.size = length;
    }
}
