# ConcurrentTest
 protected final int tryAcquireShared(int unused) {
            /*
             * Walkthrough:
             * 1. If write lock held by another thread, fail.
             * 2. Otherwise, this thread is eligible for
             *    lock wrt state, so ask if it should block
             *    because of queue policy. If not, try
             *    to grant by CASing state and updating count.
             *    Note that step does not check for reentrant
             *    acquires, which is postponed to full version
             *    to avoid having to check hold count in
             *    the more typical non-reentrant case.
             * 3. If step 2 fails either because thread
             *    apparently not eligible or CAS fails or count
             *    saturated, chain to version with full retry loop.
             */
            Thread current = Thread.currentThread();
            int c = getState();
            if (exclusiveCount(c) != 0 &&
                getExclusiveOwnerThread() != current)  // 如果写锁被占领了且不是当前线程占领，那么直接返回 -1
                return -1;
            int r = sharedCount(c); // 查询共享锁的数目
            if (!readerShouldBlock() && // 如果第一个加入队列的是独享锁即写锁，返回true
                r < MAX_COUNT && 	// 共享的数据不能超过65535
                compareAndSetState(c, c + SHARED_UNIT)) {  // cas设置state
                if (r == 0) {
                    firstReader = current;
                    firstReaderHoldCount = 1;
                } else if (firstReader == current) {
                    firstReaderHoldCount++;
                } else {
                    HoldCounter rh = cachedHoldCounter;
                    if (rh == null || rh.tid != getThreadId(current))  // 如果两个读线程都在申请共享锁，就会走到这个里面
                        cachedHoldCounter = rh = readHolds.get(); // 获取到当前线程的HoldCounter
                    else if (rh.count == 0)
                        readHolds.set(rh);
                    rh.count++; // 计算重入的次数
                }
                return 1;
            }
            return fullTryAcquireShared(current);
        }
		
		// 把c的值与11111111 11111111 按位与，这样其实就是取到了写锁的重入数
		  static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }
		  
		  // 把c的值向右移16为，并且高位补0； >>> 无符号右移,高位补0
        static int sharedCount(int c)    { return c >>> SHARED_SHIFT; }
		
		
		
		   final boolean readerShouldBlock() {
            /* As a heuristic to avoid indefinite writer starvation,
             * block if the thread that momentarily appears to be head
             * of queue, if one exists, is a waiting writer.  This is
             * only a probabilistic effect since a new reader will not
             * block if there is a waiting writer behind other enabled
             * readers that have not yet drained from the queue.
             */
            return apparentlyFirstQueuedIsExclusive();
        }
		
		 /**
		 * Returns {@code true} if the apparent first queued thread, if one
		 * exists, is waiting in exclusive mode.  If this method returns
		 * {@code true}, and the current thread is attempting to acquire in
		 * shared mode (that is, this method is invoked from {@link
		 * #tryAcquireShared}) then it is guaranteed that the current thread
		 * is not the first queued thread.  Used only as a heuristic in
		 * ReentrantReadWriteLock.
		 */
		 // 如果第一个加入队列的是独享锁，返回true
    final boolean apparentlyFirstQueuedIsExclusive() {
        Node h, s;
        return (h = head) != null &&
            (s = h.next)  != null &&
            !s.isShared()         &&
            s.thread != null;
    }
	
	
	final int fullTryAcquireShared(Thread current) {
            /*
             * This code is in part redundant with that in
             * tryAcquireShared but is simpler overall by not
             * complicating tryAcquireShared with interactions between
             * retries and lazily reading hold counts.
             */
            HoldCounter rh = null;
            for (;;) {
                int c = getState();
                if (exclusiveCount(c) != 0) {
                    if (getExclusiveOwnerThread() != current)
                        return -1;
                    // else we hold the exclusive lock; blocking here
                    // would cause deadlock.
                } else if (readerShouldBlock()) {
                    // Make sure we're not acquiring read lock reentrantly
                    if (firstReader == current) {
                        // assert firstReaderHoldCount > 0;
                    } else {
                        if (rh == null) {
                            rh = cachedHoldCounter;
                            if (rh == null || rh.tid != getThreadId(current)) {
                                rh = readHolds.get();
                                if (rh.count == 0)
                                    readHolds.remove();
                            }
                        }
                        if (rh.count == 0)
                            return -1;
                    }
                }
                if (sharedCount(c) == MAX_COUNT)
                    throw new Error("Maximum lock count exceeded");
                if (compareAndSetState(c, c + SHARED_UNIT)) {
                    if (sharedCount(c) == 0) {
                        firstReader = current;
                        firstReaderHoldCount = 1;
                    } else if (firstReader == current) {
                        firstReaderHoldCount++;
                    } else {
                        if (rh == null)
                            rh = cachedHoldCounter;
                        if (rh == null || rh.tid != getThreadId(current))
                            rh = readHolds.get();
                        else if (rh.count == 0)
                            readHolds.set(rh);
                        rh.count++;
                        cachedHoldCounter = rh; // cache for release
                    }
                    return 1;
                }
            }
        }
		
		