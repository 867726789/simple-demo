package org.example.threadpool;

import org.example.threadpool.reject.RejectHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MyThreadPool {

    class Worker implements Runnable {
        boolean isCore;
        Thread thread;
        public Worker(boolean isCore) {
            this.isCore = isCore;
            thread = new Thread(this);
            thread.start();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Runnable task = isCore ? taskQueue.take() : taskQueue.poll(keepAliveTime,timeUnit);
                    if (task != null) {
                        task.run();
                    } else {
                        if (!isCore) {
                            break;
                        }
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                workerList.remove(this);
                System.out.println(Thread.currentThread().getName()+"结束了");
            }
        }
    }

    private int corePoolSize = 4;
    private int maxPoolSize = 4;
    private int keepAliveTime = 10;
    private TimeUnit timeUnit;
    private BlockingQueue<Runnable> taskQueue;
    private List<Worker> workerList;
    private RejectHandler rejectHandler;

    public BlockingQueue<Runnable> getTaskQueue() {
        return taskQueue;
    }

    public MyThreadPool(int corePoolSize, int maxPoolSize, int keepAliveTime, TimeUnit timeUnit, BlockingQueue<Runnable> taskQueue, RejectHandler rejectHandler) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.taskQueue = taskQueue;
        this.workerList = new ArrayList<Worker>();
        this.rejectHandler = rejectHandler;
    }

    public void execute(Runnable task) {
        if (workerList.size() < corePoolSize) {
            workerList.add(new Worker(true));
        }
        if (taskQueue.offer(task)) {
            return;
        }
        if (workerList.size() < maxPoolSize) {
            workerList.add(new Worker(false));
        }
        if (!taskQueue.offer(task)) {
            rejectHandler.reject(task,this);
        }
    }



}
