package com.study.etc.concurrent.day2;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

class ReentrantLockTest {

    Logger logger = LoggerFactory.getLogger(ReentrantLockTest.class);

    class Counter {
        private final ReentrantLock lock = new ReentrantLock();
        private int count = 0;

        public void increament() {
            IntStream.range(1, 10000)
                    .forEach(i -> {
                        lock.lock();

                        try {
                            this.count++;
                        } finally {
                            lock.unlock();
                        }
                    });
        }
        
        public void increamentTryLock() {
            if (lock.tryLock()) {
                try {
                    logger.info("[increamentTryLock] thread :: {}", Thread.currentThread().getName());
                    this.count++;
                } finally {
                    lock.unlock();
                }
            } else {
                logger.info("[increamentTryLock] Lock 을 획득하지 못함");
            }
        }

        public void increamentTryLockTimeout() {
            try {
                if (lock.tryLock(1, TimeUnit.SECONDS)) {
                    try {
                        Thread.sleep(1500);
                        this.count++;
                    } finally {
                        lock.unlock();
                    }
                } else {
                    logger.info("[increamentTryLockTimeout] 1초 이내에 Lock 을 획득하지 못함");
                }
            } catch (InterruptedException e) {
                logger.error("[InterruptedException] {}", e.getMessage());
            }
        }

        public int getCount() {
            return this.count;
        }
    }

    @Test
    void testCounter() throws InterruptedException {
        Counter counter = new Counter();

        Thread t1 = new Thread(() -> counter.increament());
        Thread t2 = new Thread(() -> counter.increament());

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        logger.info("[testCounter] count :: {}", counter.getCount());
    }

    @Test
    void testCounterTryLock() throws InterruptedException {
        Counter counter = new Counter();

        Thread t1 = new Thread(counter::increamentTryLock);
        Thread t2 = new Thread(counter::increamentTryLock);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        logger.info("[testCounterTryLock] count :: {}", counter.getCount());
    }

    @Test
    void testCounterTryLockTimeout() throws InterruptedException {
        Counter counter = new Counter();

        Thread t1 = new Thread(counter::increamentTryLockTimeout);
        Thread t2 = new Thread(counter::increamentTryLockTimeout);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        logger.info("[testCounterTryLock] count :: {}", counter.getCount());
    }
}
