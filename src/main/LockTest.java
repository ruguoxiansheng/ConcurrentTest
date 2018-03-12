package main;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2018/3/8.
 * 这个测试是为了试验，公平锁与非公平锁入队列之后的操作顺序。
 * 结果表明，公平锁与非公平锁一旦入到队列之后，会按照顺序一个一个解锁操作
 * 非公平锁的非公平性就体现在其他线程会与入队的线程进行竞争锁。
 */
public class LockTest {

    private ReentrantLockMine reentrantLock = new ReentrantLockMine(false);

    public void query() {
        try {
            System.out.println("线程："+Thread.currentThread()+"开始拿锁！");
            reentrantLock.lock();
            System.out.println("线程："+Thread.currentThread()+"拿到锁，开始睡眠！");
            Thread.sleep(10000l);
            System.out.println("线程队列中等待的线程："+   reentrantLock.getQueueLength()+";"+reentrantLock.getQueuedThreads());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
            System.out.println("线程："+Thread.currentThread()+"释放锁！");
        }
    }

    public static void main(String[] args) {
        LockTest lt = new LockTest();
        System.out.println("开始执行！");
        for (int i =0 ; i < 4;i++) {
            new Thread(){
                public void run() {
                    lt.query();
                };
            }.start();
        }
    }
}
