package ru.progwards.java2.lessons.basetypes;

import java.util.Iterator;
import java.util.ListIterator;

public class BiDirList<T> {

    class ListItem<T> {

        private T item;
        private ListItem<T> next;
        private ListItem<T> prev;

        ListItem(T item) {
            this.item = item;
        }

        T getItem() {
            return item;
        }

        void setNext(ListItem<T> item) {
            next = item;
        }

        void setPrev(ListItem<T> item) {
            prev = item;
        }

        ListItem<T> getNext() {
            return next;
        }

        ListItem<T> getPrev() {
            return prev;
        }

    }

    private ListItem<T> head;
    private ListItem<T> tail;

    ListItem<T> getHead() {
        return head;
    }

    ListItem<T> getTail() {
        return tail;
    }

    public void addLast(T item) { // добавить в конец списка
        ListItem<T> li = new ListItem<T>(item);
        if (head == null) {
            head = li;
            tail = li;
        } else {
            tail.setNext(li);
            li.setPrev(tail);
            tail = li;
        }
    }

    public void addFirst(T item) { // добавить в начало списка
        ListItem<T> li = new ListItem<T>(item);
        if (head == null) {
            head = li;
            tail = li;
        } else {
            head.setPrev(li);
            li.setNext(head);
            head = li;
        }
    }

    public void remove(T item) { // удалить
        ListItem<T> current = getHead();
        while(current != null) {
            if(item.equals(current.getItem())) {
                ListItem<T> prevItem = current.getPrev();
                if(prevItem != null) {
                    prevItem.setNext(current.getNext());
                }
                ListItem<T> nextItem = current.getNext();
                if(nextItem != null) {
                    nextItem.setPrev(current.getPrev());
                }
                return;
            }
            current = current.getNext();
        }
    }

    public T at(int i) { // получить элемент по индексу
        if(i >= size()) {
            throw new IndexOutOfBoundsException();
        }

        int counter = 0;
        ListItem<T> current = getHead();
        while(current != null) {
            if(counter == i) {
                return current.getItem();
            }
            current = current.getNext();
            counter++;
        }
        return null;
    }

    public int size() { // получить количество элементов
        int counter = 0;
        ListItem<T> current = getHead();
        while(current != null) {
            current = current.getNext();
            counter++;
        }
        return counter;
    }

    public static<T> BiDirList<T> from(T[] array) { // конструктор из массива
        BiDirList<T> list = new BiDirList<>();
        for(T elem : array) {
            list.addLast(elem);
        }
        return list;
    }

    public static<T> BiDirList<T> of(T...array) { //  конструктор из массива
        return BiDirList.from(array);
    }

    public T[] toArray(T[] array) { // скопировать в массив
        int sizeOfList = size();
        if (array.length < sizeOfList) {
            array = (T[]) new Object[sizeOfList];
        }
        ListItem<T> elem = getHead();
        for(int i = 0; i < array.length; i++) {
            if (i > sizeOfList) {
                array[i] = null;
            } else {
                array[i] = elem.getItem();
                elem = elem.getNext();
            }
        }
        return array;
    }

    public Iterator<BiDirList<T>> getIterator() { // получить итератор
        return new ListIterator<BiDirList<T>>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public BiDirList<T> next() {
                return null;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public BiDirList<T> previous() {
                return null;
            }

            @Override
            public int nextIndex() {
                return 0;
            }

            @Override
            public int previousIndex() {
                return 0;
            }

            @Override
            public void remove() {

            }

            @Override
            public void set(BiDirList<T> tBiDirList) {

            }

            @Override
            public void add(BiDirList<T> tBiDirList) {

            }
        }
    }
}
