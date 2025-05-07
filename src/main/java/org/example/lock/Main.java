package org.example.lock;

import java.util.ArrayList;
import java.util.List;

public class Main {
    static int count = 1000;
    static MyLock lock = new MyLock();
    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0 ; i < 100 ; i ++) {
            threads.add(new Thread(()->{
                for (int j = 0 ; j < 10 ; j ++) {
                    lock.lock();
                    lock.lock();
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    count--;
                    lock.unlock();
                    lock.unlock();
                }
            }));
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(count);

    }
}
