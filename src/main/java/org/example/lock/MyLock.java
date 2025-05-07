package org.example.lock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class MyLock {

    class Node {
         Node pre;
         Node next;
        Thread thread;
    }


    AtomicInteger state = new AtomicInteger(0);

    Thread owner = null;

    AtomicReference<Node> head = new AtomicReference<>(new Node());
    AtomicReference<Node> tail = new AtomicReference<>(head.get());

    void lock(){
        // 可重入
        if (owner == Thread.currentThread()) {
            state.incrementAndGet();
            return;
        }

        // 这一段是非公平锁的实现，注释可以使任何节点都得加入等待队列再被唤醒，从而实现公平锁
        if (state.compareAndSet(0, 1)) {
            owner = Thread.currentThread();
            System.out.println(owner.getName()+"获得锁");
            return;
        }
        Node current = new Node();
        current.thread = Thread.currentThread();

        // 插入等待队列
        while (true) {
            Node currentTail = tail.get();
            if (tail.compareAndSet(currentTail, current)) {
                current.pre = currentTail;
                currentTail.next = current;
                break;
            }
        }

        //等待唤醒
        while (true) {
            // 先尝试获取锁，防止错过唤醒
            if (current.pre == head.get() && state.compareAndSet(0, 1)) {
                owner = Thread.currentThread();
                head.set(current);
                current.pre.next = null;
                current.pre = null;
                return;
            }
            LockSupport.park();
        }

    }


    void unlock(){
        if (Thread.currentThread() != owner) {
            throw new IllegalStateException("当前线程未持有锁");
        }
        System.out.println(owner.getName()+"释放锁");
        Node headNode = head.get();
        Node next = headNode.next;
        int currentState = state.decrementAndGet();
        if (currentState == 0) {
            owner = null;
            if (next !=null){
                LockSupport.unpark(next.thread);
            }
        }
    }
}
