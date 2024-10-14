package com.study.etc.concurrent.day2;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantReadWriteLock;

class ReentrantReadWriteLockTest {

    class ReadWriteLockEx {
        private static final Logger logger = LoggerFactory.getLogger(ReadWriteLockEx.class);
        private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        private final ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();
        private final ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();

        public void writeData() {
            writeLock.lock();
            try {
                logger.info("[writeData] Thread :: {}", Thread.currentThread().getName());
            } finally {
                writeLock.unlock();
            }
        }

        public void readData() {
            readLock.lock();
            try {
                logger.info("[readData] Thread :: {}", Thread.currentThread().getName());
            } finally {
                readLock.unlock();
            }
        }
    }

    @Test
    void testReadWriteLock() throws InterruptedException {
        ReadWriteLockEx ex = new ReadWriteLockEx();
        Runnable readData = ex::readData;
        Runnable writeData = ex::writeData;

        Thread wt1 = new Thread(writeData, "Writer-1");
        Thread rt1 = new Thread(readData, "Reader-1");
        Thread rt2 = new Thread(readData, "Reader-2");

        wt1.start();
        rt1.start();
        rt2.start();

        wt1.join();
        rt1.join();
        rt2.join();
    }
}
