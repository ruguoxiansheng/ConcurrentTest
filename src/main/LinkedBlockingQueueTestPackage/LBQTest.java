package main.LinkedBlockingQueueTestPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 2018/3/26.
 *示例目标：调试将队列添加到队列中的过程
 *
 */
public class LBQTest {

    public  static  void main(String[] args) {
        List<String> aList = new ArrayList<>();
        aList.add("a");
        aList.add("b");
        aList.add("c");
        LinkedBlockingQueue lbq = new LinkedBlockingQueue(aList);
        System.out.println(lbq.toString());
    }
}
