**CountDownLatch**

  /**
     * Inserts the specified element at the tail of this queue, waiting if
     * necessary for space to become available.
     *插入元素在队列尾部，一直等待直到有存储空间
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
     // 在LBQ(LinkedBlockingQueue)队列一直没有装满的条件下，锁的竞争与数据的删除分析的比较简单，这里主要分析
     // 在LBQ装满的情况下锁的竞争。
     // 假设LBQ的容量为2，有5个线程要往里面添加数据，并且都已经在syncQueue队列中排列好了，此时syncQueue队列中有3个等待线程节点，假设分别为t1,t2,t3
     // 
        putLock.lockInterruptibly();
        try {
              // 当队列中存入的数据与容量大小一样时，需要将线程加入到等待队列中
              // 当第二个线程插入数据并且成功释放锁之后，syncQueue中的第三个线程t1成功来到了这里
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
        // 释放putLock锁，释放锁之后，会通知syncQueue队列中的线程节点t1，（如果没有竞争的情况下且没有中断）走下面的代码获取到锁，然后t1节点从syncQueue队列中移除了
        /**
        *   final Node p = node.predecessor();
               *          if (p == head && tryAcquire(arg)) {
               *              setHead(node);调用这个方法把线程的头结点改变成当前节点，并且把当前节点的thread=null,prev=null
                *             p.next = null; // help GC
                 *            failed = false;
                  *           return;
                *         }
        */
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
    // 将t1线程加入等待队列即条件队列（ConditionQueue）中，并把锁传递给syncQueue中的下一个节点t2，在没有意外的情况下，t2会沿着t1的道路走一遍
      public final void await() throws InterruptedException {
                if (Thread.interrupted())
                    throw new InterruptedException();
                    // 将当前线程往ConditinoQueue等待队列中添加
                Node node = addConditionWaiter();
                // 添加完成之后，并把锁传递给syncQueue中的下一个节点t2，在没有意外的情况下，t2会沿着t1的道路走一遍
                //如果LBQ队列中的元素一直没有被删除，那么请求锁所有线程都被添加到等待队列ConditionQueue中。(LBQTest3)
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

// 判断节点是否在同步队列syncQueue中
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
                // 如果第一个为空，说明没有等待的线程(在LBQ没有满的时候，first==null)，否则doSignal(first)
                if (first != null)
                    doSignal(first);
            }
 // 通知ConditionQueue中的头节点，并将头节点放置到SyncQueue中。如果SyncQueue队列中已经是空，那么ConditionQueue头节点就会
          // 被添加到SyncQueue中，然后锁被释放，被移动到SyncQueue队列中的节点获取到锁之后开始插入数据。
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

// 把ConditionQueue中的节点参数再移动到SyncQueue中(LBQTest4)
// 如果此时SyncQueue中存在线程节点，
//那么从ConditionQueue队列中移送到SyncQueue队列中的节点就会被添加在结尾，
//但是这个节点阻塞却在await()方法中。
//此时存在的问题没有想通，情景如下：
//在SyncQueue存在2个线程等待的节点，ConditionQueue的头节点t，在remove()方法执行之后被
//加入到SyncQueue队列的末尾，但是t的线程却在await()方法中的acquirequeue方法中阻塞，当SyncQueue队列中的
//线程获取到锁之后继续执行的话，t节点拿到锁执行，会不会有问题。(我重写了LBQ，在LBQTest5验证这个问题)

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
// 设置成功之后ws=0,设置前一个节点的状态为-1。如果线程节点被取消了，那么取消线程的阻塞。
if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
LockSupport.unpark(node.thread);
return true;
}






// offer(Object o),往队列的尾部插入数据，如果队列满了，直接返回false

public boolean offer(E e) {
// 如果插入的数据为空，抛出异常
if (e == null) throw new NullPointerException();
final AtomicInteger count = this.count;
// 如果队列满了，返回false
if (count.get() == capacity)
    return false;
int c = -1;
Node<E> node = new Node<E>(e);
final ReentrantLock putLock = this.putLock;
// 线程在此拿锁，如果拿不到锁就会加入到synqueue,并且阻塞住
putLock.lock();
// 节点拿到锁
try {
// 如果count < capacity，就将节点入队列
    if (count.get() < capacity) {
    // 将节点入链表末尾
        enqueue(node);
        // 
        c = count.getAndIncrement();
        if (c + 1 < capacity)
        // 如果链表没有满，就会通知ConditionQueue的节点
        // 如果在一个方法中只调用了offer这个方法，那么ConditionQueue队列是空的，notFull.signal()是为了
        //防止调用了其他的方法，会往ConditionQueue节点添加数据
            notFull.signal();
    }
} finally {
    putLock.unlock();
}
if (c == 0)
// 在添加了一条数据成功之后，要通知taken队列链表已经不是空的了，可以取数据了
    signalNotEmpty();
return c >= 0;
}

   /**
     * Signals a waiting take. Called only from put/offer (which do not
     * otherwise ordinarily lock takeLock.)
     */
    private void signalNotEmpty() {
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
        // 注意这里通知的队列是token队列
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }
    
    
/**
*如果在规定的时间内等待队列中有空间，插入指定的数据在队列的末尾
* Inserts the specified element at the tail of this queue, waiting if
* necessary up to the specified wait time for space to become available.
*如果成功，返回true，如果在指定的时间内没有获取到空间，则返回false
* @return {@code true} if successful, or {@code false} if
*         the specified waiting time elapses before space is available
* @throws InterruptedException {@inheritDoc}
* @throws NullPointerException {@inheritDoc}
*/
public boolean offer(E e, long timeout, TimeUnit unit)
throws InterruptedException {

if (e == null) throw new NullPointerException();
// 将制定单位的时间换算成纳秒的形式
long nanos = unit.toNanos(timeout);
int c = -1;
final ReentrantLock putLock = this.putLock;
final AtomicInteger count = this.count;
// 获取到锁，支持中断，线程中断之后抛出异常
putLock.lockInterruptibly();
try {
// 如果队列已经满了
while (count.get() == capacity) {
// 并且等待时间《=0，那么直接返回false
    if (nanos <= 0)
        return false;
    nanos = notFull.awaitNanos(nanos);
}
enqueue(new Node<E>(e));
c = count.getAndIncrement();
if (c + 1 < capacity)
    notFull.signal();
} finally {
putLock.unlock();
}
if (c == 0)
signalNotEmpty();
return true;
}



public final long awaitNanos(long nanosTimeout)
                throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            // 用等待的时间+系统时间=最后的时刻期限
            final long deadline = System.nanoTime() + nanosTimeout;
            int interruptMode = 0;
            // 如果节点不在等待都列中
            while (!isOnSyncQueue(node)) {
            // 如果等待的最后期限《0
                if (nanosTimeout <= 0L) {
                    transferAfterCancelledWait(node);
                    break;
                }
                if (nanosTimeout >= spinForTimeoutThreshold)
                    LockSupport.parkNanos(this, nanosTimeout);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
                nanosTimeout = deadline - System.nanoTime();
            }
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null)
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
            return deadline - System.nanoTime();
        }









