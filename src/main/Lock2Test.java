package main;

import java.util.SortedSet;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by Administrator on 2018/3/12.
 * 这个是为了测试LockSupport.park()方法与LockSupport.unpart()
 */
public class Lock2Test {

    public static void main(String[] arg) {

        Thread thread1 = new Thread() {
            @Override
            public void run() {
                System.out.println("打游戏线程！");
                LockSupport.park();
                System.out.println("结束游戏线程！");
            }

        };
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                System.out.println("看书线程！");
                LockSupport.park();
                System.out.println("结束看书线程！");
            }

        };

        Thread thread3 = new Thread() {
            @Override
            public void run() {
                System.out.println("回家线程！");
                LockSupport.park();
                System.out.println("结束回家线程！");
            }

        };

        thread1.start();
        thread2.start();
        thread3.start();
        try {
            Thread.sleep(3000l);
            System.out.println("选择一个线程调用");
            LockSupport.unpark(thread2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
