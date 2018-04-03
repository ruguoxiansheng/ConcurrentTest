package main.LinkedBlockingQueueTestPackage;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 2018/3/26.
 *示例目标：在LBQ队列填满之后，再添加数据的线程会被添加到ConditionQueue中
 */
public class LBQTest3 {

    public  static  void main(String[] args) {
        LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<String>(2);
        try {
            lbq.put("a");
            lbq.put("b");
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
            Thread.sleep(3000l);
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
            Thread.sleep(3000l);
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

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(lbq.toString());
    }
}
