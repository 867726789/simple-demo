package org.example.schedule;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        ScheduledService scheduledService = new ScheduledService();
        scheduledService.schedule(()->{
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            String currentTime = sdf.format(new Date());
            System.out.println("输出1-当前时间: " + currentTime);
        },100);
        System.out.println("主函数正常1");
        scheduledService.schedule(()->{
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            String currentTime = sdf.format(new Date());
            System.out.println("输出2-当前时间: " + currentTime);
        },200);
        System.out.println("主函数正常2");
    }
}