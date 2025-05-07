package org.example.threadpool.reject;

import org.example.threadpool.MyThreadPool;

public class AbortPolicy implements RejectHandler {

    @Override
    public void reject(Runnable task, MyThreadPool myThreadPool) {
        throw new RuntimeException("线程池繁忙");
    }
}
