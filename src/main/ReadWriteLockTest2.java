package main;

/**
 * Created by Administrator on 2018/3/19.
 * 为了测试锁的等待队列的溢出
 */
public class ReadWriteLockTest2 {

    public static  void main(String[] args) {
            while (true) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100000l);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
    }

}
