package main.LinkedBlockingQueueTestPackage;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class LBQOfferTest1 {
    public static void main(String[] args){
        LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<>(3);
        lbq.offer("a");
        lbq.offer("b");
        lbq.offer("d");

        Thread thread1 = new Thread() {
            public void run() {
                try {
                    lbq.offer("e",1, TimeUnit.SECONDS);
                    System.out.println("超时推出！");
                } catch (InterruptedException e) {
                  System.out.println("aa");
                }
            }
        };
        thread1.setName("thread-1");
        thread1.start();
        try {
            Thread.sleep(3000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
