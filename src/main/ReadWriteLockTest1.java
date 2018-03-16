package main;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Administrator on 2018/3/14.
 *这个是为了测试readHolds这个属性里面存储的内容
 *
 */
public class ReadWriteLockTest1 {


    public  static  void main(String[] args) {
         ReentrantReadWriteLock rrw = new ReentrantReadWriteLock();
        ReadWriteLockTest1 rwt = new ReadWriteLockTest1();
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                    rrw.readLock().lock();
                    try {
                        System.out.println("thread="+Thread.currentThread().getName()+" is sleep!");
                        Thread.sleep(100000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        rrw.readLock().unlock();
                    }
            }
        };
        thread1.setName("thread1");
        // 线程1 先启动，占领了读锁，然后就一直休眠
        thread1.start();
        try {
            Thread.sleep(3000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                try {
                   rrw.readLock().lock();
                    rrw.readLock().lock();
                    System.out.println("thread="+Thread.currentThread().getName()+" is sleep!");
                      Thread.sleep(100000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        rrw.readLock().unlock();
                    }
            }
        };
        // 线程2启动之后会去拿两次读锁，所以在HoldCounter中的count=2，然后会休眠

        Thread thread3 = new Thread() {
            @Override
            public void run() {
                try {
                    rrw.readLock().lock();
                    System.out.println("thread="+Thread.currentThread().getName()+" is sleep!");
                    Thread.sleep(3000l);
                    // 线程3第二次拿到锁之后，执行rh == null || rh.tid != getThreadId(current)
                    // 然后走到  cachedHoldCounter = rh = readHolds.get();此时会把cachedHoldCounter从线程2切换到线程3
                    rrw.readLock().lock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    rrw.readLock().unlock();
                }
            }
        };
    System.out.println("读线程开始工作！");
        thread2.setName("thread2");
        thread3.setName("thread3");
        thread2.start();
        thread3.start();


    }

}
