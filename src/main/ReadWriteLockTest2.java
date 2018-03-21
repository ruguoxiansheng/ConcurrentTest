package main;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Administrator on 2018/3/19.
 * 测试目的：线程1占领读锁，并一直占领，线程2申请读锁，是不是走下面这个分支
 * else if (rh.count == 0)
 * readHolds.set(rh);
 */
public class ReadWriteLockTest2 {
    static ReentrantReadWriteLock rrw = new ReentrantReadWriteLock();
    public static void main(String[] args) {
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                ReentrantReadWriteLock.ReadLock rl = rrw.readLock();
                rl.lock();
                System.out.println(rrw.readLock());
            }
        };
        thread1.setName("thread1");
        thread1.start();

        Thread thread2 = new Thread() {
            @Override
            public void run() {
                rrw.readLock().lock();
                System.out.println(rrw.readLock());
                rrw.readLock().unlock();
                rrw.readLock().lock();
            }
        };
        thread2.setName("thread2");
        thread2.start();

//        Thread thread3 = new Thread() {
//            @Override
//            public void run() {
//                rrw.readLock().lock();
//                System.out.println(rrw.readLock());
//            }
//        };
//        thread3.setName("thread3");
//        thread3.start();
    }
}
