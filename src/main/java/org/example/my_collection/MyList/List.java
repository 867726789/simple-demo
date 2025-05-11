package org.example.my_collection.MyList;

public interface List<E> extends Iterable<E> {

    void add(E element);

    void add(int index, E element);

    E remove(int index);

    boolean remove(E element);

    E set(int index, E element);

    E get(int index);

    int size();
}
