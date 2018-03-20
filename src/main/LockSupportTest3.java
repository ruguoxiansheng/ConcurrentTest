package main;

import java.util.concurrent.locks.LockSupport;

/**
 * Created by Administrator on 2018/3/19.
 * 这个类是用于测试，parkBlocker
 */
public class LockSupportTest3 {
    public static void main(String[] args) {
        BlockObj bl = new BlockObj();
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                System.out.println("打游戏线程！");
                LockSupport.park(bl);
                System.out.println("结束游戏线程！");
            }

        };
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                System.out.println("看书线程！");
                LockSupport.park(bl);
                System.out.println("结束看书线程！");
            }
        };

        thread1.start();
        try {
            Thread.sleep(10000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread2.start();

        try {
            Thread.sleep(3000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LockSupport.unpark(thread1);
    }
}
