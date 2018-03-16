package main;

import java.util.concurrent.locks.LockSupport;

/**
 * Created by Administrator on 2018/3/13.
 * 1、这个是为了验证interrupt对park的影响，还有线程中interrupt状态的影响
 * 结果表明，LockSupport能响应Thread.interrupt()事件，但是不会抛出InterruptedException异常。
 *线程1准备拿锁！
 *is interrupt1:false
 *is interrupt2:true
 *is interrupt3:true
 *
 * 2、LockSupport.unpark()也会中断park的阻塞，但是不会更改线程中interrupt这个状态
 * 线程1准备拿锁！
 *is interrupt1:false
 *is interrupt2:false
 *is interrupt3:false
 *
 */
public class LockSupportTest {
    public static  void main (String[] args) {

        Thread thread1 = new Thread() {
            @Override
            public void run() {
                System.out.println("线程1准备拿锁！");
                LockSupport.park(this);
                System.out.println("is interrupt3:" +Thread.interrupted());
            }
        };
        thread1.start();
        try {
            Thread.sleep(3000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("is interrupt1:"+thread1.isInterrupted());
//        LockSupport.unpark(thread1);
        thread1.interrupt();
        System.out.println("is interrupt2:"+thread1.isInterrupted());

    }
}
