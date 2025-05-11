package org.example.my_collection.MyHashMap;

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        MyHashMap<Integer,Integer> hashMap = new MyHashMap<>(10);
        int count = 100000;
        for (int i = 0; i < count; i++) {
            hashMap.put(i,i);
        }
        for (int i = 0; i < count; i++) {
            hashMap.get(i);
//            System.out.println(hashMap.get(i));
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("执行耗时: " + duration + "毫秒");
    }
}
