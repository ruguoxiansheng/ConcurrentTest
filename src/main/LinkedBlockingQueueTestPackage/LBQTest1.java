package main.LinkedBlockingQueueTestPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 2018/3/26.
 *示例目标：测试put中锁的行走机制
 * 调试方式：
 *  先将断点断在lbq.put("c");处，等个3秒中然后再走。
 *
 */
public class LBQTest1 {

    public  static  void main(String[] args) {
        LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<String>(2);
        try {
            lbq.put("a");
            lbq.put("b");
//             Thread thread1 = new Thread() {
//                public void run() {
//                    try {
//                        lbq.put("c");
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//             thread1.setName("thread-1");
//             thread1.start();
//            Thread.sleep(3000l);
//            Thread thread2 = new Thread() {
//                public void run() {
//                    try {
//                        lbq.put("d");
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//            thread2.setName("thread-2");
//            thread2.start();

//            Thread thread3 = new Thread() {
//                public void run() {
//                    try {
//                        lbq.put("e");
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//            thread3.setName("thread-3");
//            thread3.start();
////            lbq.put("c");
//            Thread.sleep(3000l);
//            lbq.put("e");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(lbq.toString());
    }
}
