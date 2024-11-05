package com.study.etc.concurrent.etc;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class SpinLockTest {

    private final SpinLock lock = new SpinLock();
    private int sharedCounter = 0;

    static class SpinLock {
        private final AtomicBoolean isLocked = new AtomicBoolean(false);

        public void lock() {
            while (!isLocked.compareAndSet(false, true)) {}
        }
        
        public void unlock() {
            isLocked.set(false);
        }
    }

    public void incrementCounter() {
        lock.lock();
        
        try {
            sharedCounter++;
        } finally {
            lock.unlock();
        }
    }

    @Test
    void 스핀락_테스트() throws InterruptedException {
        int threadCnt = 100;

        ExecutorService executor = Executors.newFixedThreadPool(threadCnt);

        for (int i = 0; i < threadCnt; i++) {
            executor.submit(this::incrementCounter);
        }

        executor.shutdown();

        while (!executor.isTerminated()) {
            Thread.sleep(10);
        }

        assertThat(sharedCounter).isEqualTo(threadCnt);
    }
}
