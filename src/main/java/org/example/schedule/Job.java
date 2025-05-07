package org.example.schedule;

public class Job implements Comparable<Job>{
    private Runnable task;
    private long startTime;
    private long delay;

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public Runnable getTask() {
        return task;
    }

    public void setTask(Runnable task) {
        this.task = task;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public int compareTo(Job o) {
        return Long.compare(startTime, o.getStartTime());
    }
}
