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
        // 多个线程在此处竞争锁，这里是非公平竞争，竞争成功的拿到锁之后往下面走，没有竞争成功的加入到线程节点的队列中
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
                while (!isOnSyncQueue(node)) {
                // 请求的线程都阻塞在这里
                    LockSupport.park(this);
                    if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                        break;
                }
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

// 判断节点是否在同步队列中
   final boolean isOnSyncQueue(Node node) {
                if (node.waitStatus == Node.CONDITION || node.prev == null)
                    return false;
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
                return findNodeFromTail(node);
            }













