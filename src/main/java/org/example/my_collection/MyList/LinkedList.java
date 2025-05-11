package org.example.my_collection.MyList;


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class LinkedList<E> implements List<E> {

    private int size;

    private Node<E> head;
    private Node<E> tail;


    @Override
    public void add(E element) {
        Node<E> node = new Node<>(element,tail,null);
        if (tail != null) {
            tail.next = node;
        } else {
            head = node;
        }
        tail = node;
        size++;
    }

    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        if (index == size) {
            add(element);
            return;
        }
        Node<E> indexNode = findNode(index);
        Node<E> node = new Node<>(element,indexNode.prev,indexNode);
        if (indexNode.prev == null) {
            head = node;
        } else {
            indexNode.prev.next = node;
        }
        indexNode.prev = node;
        size++;
    }

    private Node<E> findNode(int index) {
        Node<E> result = null;
        if (index < size / 2) {
            result = head;
            for (int i = 0 ; i < index; i++) {
                result = result.next;
            }
        } else {
            result = tail;
            for (int i = size - 1 ; i > index; i--) {
                result = result.prev;
            }
        }
        return result;
    }

    @Override
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        return  reomveNode(findNode(index));
    }

    private E reomveNode(Node<E> node) {
        Node<E> prev = node.prev;
        Node<E> next = node.next;
        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
        }
        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
        }
        node.prev = null;
        node.next = null;
        size--;
        return node.element;
    }

    @Override
    public boolean remove(E element) {
        Node<E> node = head;
        int index = 0;
        while (node != null) {
            if (Objects.equals(node.element, element)) {
                reomveNode(node);
                return true;
            }
            index++;
            node = node.next;
        }
        return false;
    }

    @Override
    public E set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<E> node = findNode(index);
        E oldElement = node.element;
        node.element = element;
        return oldElement;
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<E> node = findNode(index);
        return node.element;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        return new LinkedListIterator();
    }

    class Node<E> {
        Node<E> prev;
        Node<E> next;
        E element;
        public Node(E element) {
            this.element = element;
        }

        public Node(E element, Node<E> pre, Node<E> next) {
            this.element = element;
            this.prev = pre;
            this.next = next;
        }
    }

    class LinkedListIterator implements Iterator<E> {
        Node<E> current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            if (current == null) {
                throw new NoSuchElementException();
            }
            E result = current.element;
            current = current.next;
            return result;
        }
    }
}
