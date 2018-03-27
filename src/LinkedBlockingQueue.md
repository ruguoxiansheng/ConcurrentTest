**CountDownLatch**

  /**
     * Inserts the specified element at the tail of this queue, waiting if
     * necessary for space to become available.
     *
     * @throws InterruptedException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    public void put(E e) throws InterruptedException {
        if (e == null) throw new NullPointerException();
        // Note: convention in all put/take/etc is to preset local var
        // holding count negative to indicate failure unless set.
        int c = -1;
        Node<E> node = new Node<E>(e);
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        // 多个线程在此处竞争锁，这里是非公平竞争，竞争成功的拿到锁之后往下面走，没有竞争成功的加入到线程节点的队列,这里称为syncQueue中
     // 在LBQ(LinkedBlockingQueue)队列没有装满时，每次在最后释放锁之后，都会通知syncQueue队列的下一节点，
     //并且与其他获取锁的线程（有可能是插入，也有可能是删除）竞争；
     //假设1、这里有删除数据的线程参与竞争,假设方法是remove(Object o)，并且putLock被删除数据的线程给锁住了
     // 删除节点之后，释放锁，相关的线程又陷入了锁的竞争中
     // 
     // 
        putLock.lockInterruptibly();
        try {
              // 当队列中存入的数据与容量大小一样时，需要将线程加入到等待队列中
            while (count.get() == capacity) {
                notFull.await();
            }
            // 从队尾插入数据
            enqueue(node);
            //加入数据成功之后，count+1 ,方法是返回旧值
            c = count.getAndIncrement();
            if (c + 1 < capacity)
                notFull.signal();
        } finally {
        // 释放putLock锁
            putLock.unlock();
        }
        if (c == 0)
            signalNotEmpty();
    }
    //假设：往LinkedBlockingQueue队列中添加数据的线程数小于capacity,这种情况会导致condition的等待队列中不会有数据
    // 这种情况下分析
      public final void signal() {
                if (!isHeldExclusively())
                    throw new IllegalMonitorStateException();
                Node first = firstWaiter;
                // 等待队列中为空，那么就不操作
                if (first != null)
                    doSignal(first);
            }
    // 将线程节点加入等待队列中，并唤醒锁队列中的下一个节点，让其加入到等待队列中
      public final void await() throws InterruptedException {
                if (Thread.interrupted())
                    throw new InterruptedException();
                    // 将当前线程往等待队列中添加
                Node node = addConditionWaiter();
                // 添加完成之后，通知锁队列中的下一个线程节点，让其运行，这样锁的等待队列中就没有获取锁的线程在等待了。
                // 请求锁所有线程都被添加到等待队列中。
                int savedState = fullyRelease(node);
                int interruptMode = 0;
                // 如果不在syncQueue队列中，那么返回false,线程进行阻塞。
                while (!isOnSyncQueue(node)) {
                // 请求的线程都阻塞在这里
                    LockSupport.park(this);
                    if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                        break;
                }
                // 如果线程在syncQueue队列中
                if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                    interruptMode = REINTERRUPT;
                if (node.nextWaiter != null) // clean up if cancelled
                    unlinkCancelledWaiters();
                if (interruptMode != 0)
                    reportInterruptAfterWait(interruptMode);
            }


   /**
         * Adds a new waiter to wait queue.
         * @return its new wait node
         */
        private Node addConditionWaiter() {
            Node t = lastWaiter;
            // If lastWaiter is cancelled, clean out.
            if (t != null && t.waitStatus != Node.CONDITION) {
                unlinkCancelledWaiters();
                t = lastWaiter;
            }
            Node node = new Node(Thread.currentThread(), Node.CONDITION);
            if (t == null)
                firstWaiter = node;
            else
                t.nextWaiter = node;
            lastWaiter = node;
            return node;
        }

// 判断节点是否在同步队列(这个队列指的是锁队列)中
// 在调用完fullyRelease(node)方法之后，各线程就可以自由竞争锁了
// 此时有可能锁会被remove线程拿到，并且移除队列中的数据，然后调用signal方法，调用方法结束之后
// Condition队列中的节点会被加入到SyncQueue中
// 这两个队列的区别在于前者是单向的，每次添加尾节点，只会赋值nextWait，而syncQueue是一个双向的队列
   final boolean isOnSyncQueue(Node node) {
   // 节点的添加都是从尾部添加，节点的提取都是从头部提取
                if (node.waitStatus == Node.CONDITION || node.prev == null)
                    return false;
                    // 如果存在继任节点，那么一定在syncQueue中
                if (node.next != null) // If has successor, it must be on queue
                    return true;
                /*
                 * node.prev can be non-null, but not yet on queue because
                 * the CAS to place it on queue can fail. So we have to
                 * traverse from tail to make sure it actually made it.  It
                 * will always be near the tail in calls to this method, and
                 * unless the CAS failed (which is unlikely), it will be
                 * there, so we hardly ever traverse much.
                 */
                 // node刚刚才加入到syncQueue中，所以next==null,所以要从尾部往前找
                return findNodeFromTail(node);
            }
    
    
    
    
 public final void signal() {
 // 判断线程是否是独享模式
                if (!isHeldExclusively())
                    throw new IllegalMonitorStateException();
                    // 从等待队列中拿出第一个
                Node first = firstWaiter;
                // 如果第一个为空，说明没有等待的线程，否则doSignal(first)
                if (first != null)
                    doSignal(first);
            }
 // 通知头节点           
 private void doSignal(Node first) {
        do {
        // 设置first节点的nextWaiter为firstWaiter，如果是null，说明等待队列中只有一个节点
        // 这行代码改变了头节点
            if ( (firstWaiter = first.nextWaiter) == null)
                lastWaiter = null;
                // 这行代码将first这个节点与队列脱离了
            first.nextWaiter = null;
        } while (!transferForSignal(first) &&
                 (first = firstWaiter) != null);
    }

// 
final boolean transferForSignal(Node node) {
/*
 * If cannot change waitStatus, the node has been cancelled.
 */
 // 设置waitStatus =0
if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
    return false;

// 设置成功之后入线程队列，返回的是前一个节点
Node p = enq(node);
int ws = p.waitStatus;// 获取到前一个节点的ws
// 设置成功之后ws=0,设置前一个节点的状态为-1
if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
LockSupport.unpark(node.thread);
return true;
}













