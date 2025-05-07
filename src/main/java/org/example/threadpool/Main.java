package org.example.threadpool;

import org.example.schedule.ScheduledService;
import org.example.threadpool.reject.AbortPolicy;
import org.example.threadpool.reject.DiscardOldPolicy;
import org.example.threadpool.reject.DiscardPolicy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        MyThreadPool myThreadPool = new MyThreadPool(2, 4, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2), new AbortPolicy());
        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            myThreadPool.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + "执行： " + finalI);
            });
        }

        System.out.println("主函数正常");
    }
}