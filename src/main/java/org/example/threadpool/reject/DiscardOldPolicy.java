package org.example.threadpool.reject;

import org.example.threadpool.MyThreadPool;

public class DiscardOldPolicy implements RejectHandler {

    @Override
    public void reject(Runnable task, MyThreadPool myThreadPool) {
        myThreadPool.getTaskQueue().poll();
        myThreadPool.execute(task);
    }
}
