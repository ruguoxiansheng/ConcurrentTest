package main.LinkedBlockingQueueTestPackage;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 2018/3/26.
 *示例目标：当LBQ队列中的数据被删除之后，ConditionQueue队列中的线程节点会被移送到SyncQueue队列中
 * 测试结果：当LBQ队列中的数据被删除之后，ConditionQueue队列中的线程节点会被移送到SyncQueue队列中，并且在锁被释放的时候，通知到
 * SyncQueue队列中的节点unpart(node)，然后ConditionQueue节点会取消阻塞，走await（）方法中的下面代码插入数据
 *   while (!isOnSyncQueue(node)) {
 LockSupport.park(this);
 if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
 break;
 }
 *
 */
public class LBQTest4 {

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

            lbq.remove("a");
            Thread.sleep(3000l);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(lbq.toString());
    }
}
