package org.example.my_collection.MyHashMap;

import java.util.ArrayList;
import java.util.List;

public class MyHashMap<K,V> {

    private Node<K,V>[] table = null;

    private int size = 0;
    private float loadFactor = 0.75F;

    class Node<K,V> {
        Node next;
        int hash;
        K key;
        V value;
    }

    public MyHashMap(int capacity){
        table = new Node[tableSizeFor(capacity)];
    }

    public int tableSizeFor(int capacity){
        int real_capacity = 1;
        while (real_capacity < capacity){
            real_capacity <<= 1;
        }
        return real_capacity;
    }

    private int getHash(K key){
        return (key.hashCode() & 0x7fffffff) & (this.table.length-1);
    }

    public V put(K key, V value) {
        Node<K,V> currentNode = new Node<>();
        int hash = getHash(key);
        currentNode.hash = hash;
        currentNode.key = key;
        currentNode.value = value;
        Node<K,V> head = table[currentNode.hash];
        if (head == null){
            table[hash] = currentNode;
        } else {
            while (true) {
                if (head.key.equals(key)){
                    V oldValue = head.value;
                    head.value = value;
                    return oldValue;
                }
                if (head.next == null){
                    break;
                }
                head = head.next;
            }
            head.next = currentNode;
        }
        size++;
        resizeIfNecessary();
        return null;
    }

    public V get(K key) {
        int hash = getHash(key);
        Node<K,V> resultNode = table[hash];
        while (resultNode != null){
            if (resultNode.key.equals(key)){
                return resultNode.value;
            } else {
                resultNode = resultNode.next;
            }
        }
        return null;
    }

    public V remove(K key) {
        int hash = getHash(key);
        Node<K,V> resultNode = table[hash];
        Node<K,V> prevNode = resultNode;
        if (resultNode == null){
            return null;
        }
        do {
            if (resultNode.key.equals(key)){
                break;
            }
            prevNode = resultNode;
            resultNode = resultNode.next;
        } while (resultNode != null);
        if (prevNode == resultNode) {
            table[hash] = prevNode.next;
            resultNode.next = null;
            size--;
            return resultNode.value;
        } else if (resultNode != null){
            prevNode.next = resultNode.next;
            resultNode.next = null;
            size--;
            return resultNode.value;
        }
        return null;
    }

    private void resizeIfNecessary(){
        if (this.size < this.table.length* loadFactor) {
            return;
        }
        Node<K,V>[] newTable = new Node[this.table.length << 1];
        for (Node<K,V> head : this.table) {
            if (head == null){
                continue;
            }
            Node<K,V> current = head;
            while (current != null){
                current.hash = (current.key.hashCode() & 0x7fffffff) & (newTable.length-1);
                if (newTable[current.hash] == null){
                    newTable[current.hash] = current;
                    Node<K,V> next = current.next;
                    current.next = null;
                    current = next;
                } else {
                    Node<K,V> next = current.next;
                    current.next = newTable[current.hash];
                    newTable[current.hash] = current;
                    current = next;
                }
            }
        }
        this.table = newTable;

    }

    public int size() {
        return size;
    }
}
