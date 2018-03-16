package main;

/**
 * Created by Administrator on 2018/3/14.
 * 这个测试用例是为了测试ThreadLocal类的使用，这个线程的作用就是把当前线程的局部变量保存下来。
 */
public class ThreadLocalTest {
    public static class MyRunnable implements Runnable {

        private ThreadLocal threadLocal = new ThreadLocal();

        @Override
        public void run() {
            int a = (int) (Math.random() * 100D);
//            int b= (int) (Math.random() * 100D);
            System.out.println("thread name:"+Thread.currentThread().getName()+";set value1="+a/*+";value2="+b*/);
            threadLocal.set(a);
//            threadLocal.set(b);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            }
            System.out.println("thread name:"+Thread.currentThread().getName()+";get value1="+threadLocal.get()/*+";value2="+threadLocal.get()*/);
        }
    }

    public static void main(String[] args) {

        MyRunnable sharedRunnableInstance = new MyRunnable();
        Thread thread1 = new Thread(sharedRunnableInstance);
        Thread thread2 = new Thread(sharedRunnableInstance);
        thread1.setName("thread1");
        thread2.setName("thread2");

        // 两个线程，分别在自己的线程局部变量中放置了一个数，最后取出来也是不一样的。
        thread1.start();
        thread2.start();

    }
}
