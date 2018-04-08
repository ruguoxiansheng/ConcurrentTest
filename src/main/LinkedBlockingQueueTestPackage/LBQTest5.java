package main.LinkedBlockingQueueTestPackage;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 2018/3/26.
 */
public class LBQTest5 {

    public  static  void main(String[] args) {
       LBQ<String> lbq = new LBQ<String>();
        try {
            lbq.put("a");
            lbq.put("b");
            System.out.println(lbq.toString());
            // 至此队列已经被塞满了
             Thread thread1 = new Thread() {
                public void run() {
                    try {
                        lbq.put("c");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
             thread1.setName("thread-1");
             thread1.start();

            Thread.sleep(1000l);
            Thread thread2 = new Thread() {
                public void run() {
                    try {
                        lbq.put("d");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread2.setName("thread-2");
            thread2.start();
            Thread.sleep(1000);
            Thread thread3 = new Thread() {
                public void run() {
                    try {
                        lbq.put("e");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread3.setName("thread-3");
            thread3.start();

            Thread.sleep(3000);
            lbq.remove("a");


            Thread.sleep(1000);
            lbq.remove("b");
            Thread.sleep(1000);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(lbq.toString());
    }
}
