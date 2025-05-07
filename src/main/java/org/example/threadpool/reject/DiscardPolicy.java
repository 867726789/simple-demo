package org.example.threadpool.reject;

import org.example.threadpool.MyThreadPool;

public class DiscardPolicy implements RejectHandler {

    @Override
    public void reject(Runnable task, MyThreadPool myThreadPool) {
        return;
    }
}
