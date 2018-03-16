package main;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Administrator on 2018/3/14.
 * 锁降级：如果当前线程（线程1）获得了写锁，如果其他的线程（线程2）也申请了写锁，那么线程1要是再获取读锁的时候，就获取不到。
 * 因为readerShouldBlock返回的是true,所以这个方法不是锁降级的地方
 */
public class ReadWriteLockTest {


    public  static  void main(String[] args) {
         ReentrantReadWriteLock rrw = new ReentrantReadWriteLock();
        ReadWriteLockTest rwt = new ReadWriteLockTest();
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                    rrw.writeLock().lock();
                    try {
                        System.out.println("thread="+Thread.currentThread().getName()+" is sleep!");
                        Thread.sleep(3000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        rrw.readLock().lock();
                        rrw.writeLock().unlock();
                    }
            }
        };

        Thread thread2 = new Thread() {
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
        thread2.setName("thread2");
        thread1.start();
        thread2.start();

    }

}
