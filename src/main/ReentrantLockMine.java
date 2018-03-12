package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2018/3/8.
 */
public class ReentrantLockMine extends ReentrantLock {
    public ReentrantLockMine(boolean fair) {
              super(fair);
    }
          @Override
       protected Collection<Thread> getQueuedThreads() {   //获取同步队列中的线程
              List<Thread> arrayList = new ArrayList<Thread>(super.getQueuedThreads());
                Collections.reverse(arrayList);
               return arrayList;
                }

    }
