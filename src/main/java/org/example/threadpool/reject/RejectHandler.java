package org.example.threadpool.reject;

import org.example.threadpool.MyThreadPool;

public interface RejectHandler {
    void reject(Runnable task, MyThreadPool myThreadPool);
}
