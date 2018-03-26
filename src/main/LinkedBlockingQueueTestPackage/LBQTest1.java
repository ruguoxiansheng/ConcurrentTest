package main.LinkedBlockingQueueTestPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 2018/3/26.
 *示例目标：调试将队列添加到队列中的过程
 *
 */
public class LBQTest1 {

    public  static  void main(String[] args) {
        LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<String>(2);
        try {
            lbq.put("a");
            lbq.put("b");
            lbq.put("c");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(lbq.toString());
    }
}
