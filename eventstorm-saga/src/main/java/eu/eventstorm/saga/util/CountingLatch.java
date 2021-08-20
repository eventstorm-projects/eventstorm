package eu.eventstorm.saga.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public final class CountingLatch {

    private final int initialCount;

    /**
     * Synchronization control for CountingLatch.
     * Uses AQS state to represent count.
     */
    private static final class Sync extends AbstractQueuedSynchronizer {

        private Sync(final int initialState) {
            setState(initialState);
        }

        int getCount() {
            return getState();
        }

        protected int tryAcquireShared(int acquires) {
            return getState() == 0 ? 1 : -1;
        }

        protected boolean tryReleaseShared(int delta) {
            // Decrement count; signal when transition to zero
            for (; ; ) {
                final int c = getState();
                final int nextc = c + delta;
                if (nextc < 0) {
                    return false;
                }
                if (compareAndSetState(c, nextc)) {
                    return nextc == 0;
                }
            }
        }
    }

    private final Sync sync;

    public CountingLatch(int initialCount) {
        sync = new Sync(initialCount);
        this.initialCount = initialCount;
    }

    public int getInitialCount() {
        return this.initialCount;
    }

    public void increment() {
        sync.releaseShared(1);
    }

    public int getCount() {
        return sync.getCount();
    }

    public void decrement() {
        sync.releaseShared(-1);
    }

    public void resetTo(int count) {
        if (count == sync.getCount()) {
            return;
        }
        sync.releaseShared(count-sync.getCount());
    }


    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    public boolean await(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, timeUnit.toNanos(timeout));
    }

}