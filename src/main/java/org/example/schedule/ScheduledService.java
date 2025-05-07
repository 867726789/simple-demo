package org.example.schedule;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.LockSupport;

public class ScheduledService {

    Trigger trigger = new Trigger();

    ExecutorService executorService = Executors.newFixedThreadPool(6);


    void schedule(Runnable task, long delay) {
        Job job = new Job();
        job.setTask(task);
        job.setStartTime(System.currentTimeMillis()+delay);
        job.setDelay(delay);
        trigger.jobQueue.offer(job);
        trigger.wakeUp();
    }

    class Trigger {
        PriorityBlockingQueue<Job> jobQueue = new PriorityBlockingQueue<>();
        Thread thread = new Thread(()->{
            while (true) {
                //while防止被虚假唤醒
                while (jobQueue.isEmpty()) {
                    LockSupport.park();
                }
                Job job = jobQueue.peek();
                if (job.getStartTime() <= System.currentTimeMillis()) {
                    job = jobQueue.poll();
                    executorService.execute(job.getTask());
                    job.setStartTime(System.currentTimeMillis()+job.getDelay());
                    jobQueue.offer(job);
                } else {
                    LockSupport.parkUntil(job.getStartTime());
                }
            }
        });

        {
            thread.start();
            System.out.println("启动");
        }

        void wakeUp(){
            LockSupport.unpark(thread);
        }
    }
}
