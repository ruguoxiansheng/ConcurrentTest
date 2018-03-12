package main;

import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2018/3/12.
 * 这个是为了测试LockSupport.park()方法与LockSupport.unpart()
 */
public class LockInterruptTest {

    private static ReentrantLock reentrantLock = new ReentrantLock();

    public static void main(String[] arg) {

        Thread thread1 = new Thread() {
            @Override
            public void run() {
                System.out.println("线程1准备拿锁！");
                try {
                    reentrantLock.lockInterruptibly();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                     reentrantLock.unlock();
                }

            }

        };
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                System.out.println("线程2准备拿锁！");
                try {
                    reentrantLock.lockInterruptibly();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    reentrantLock.unlock();
                }
            }

        };

        Thread thread3 = new Thread() {
            @Override
            public void run() {
                System.out.println("线程3准备拿锁！");
                try {
                    reentrantLock.lockInterruptibly();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    reentrantLock.unlock();
                }
            }

        };

        thread1.start();
        thread2.start();
        thread3.start();
        try {
            Thread.sleep(3000l);
            System.out.println("中断线程！");
            thread1.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
