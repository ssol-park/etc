package com.study.etc.concurrent.day3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadContentionTest {

    Logger logger = LoggerFactory.getLogger(ThreadContentionTest.class);

    /*
    * non-sleep : 0.101 seconds.
    * sleep(1000) : 63.523 seconds.
    * */
    @Test
    @DisplayName("적절한 크기의 쓰레드풀 사용")
    void test01() {
        int threadCnt = 8;
        int tasks = 500;
        ExecutorService executor = Executors.newFixedThreadPool(threadCnt);

        Runnable task = () -> {
            long sum = 0;

//            sleep(1000);

            for (long i = 0; i < 1000000L; i++) { sum += i; } //
//            for (long i = 0; i < 100L; i++) { sum += i; }

            logger.info(Thread.currentThread().getName() + " finished.");
        };

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < tasks; i++) {
            executor.submit(task);
        }

        executor.shutdown();
        while (!executor.isTerminated()) {}

        long endTime = System.currentTimeMillis();
        logger.info("Optimal thread pool execution time: " + (endTime - startTime) / 1000.0 + " seconds.");
    }

    /*
     * threadCnt = 4000;
     *  - non-sleep : 0.25 seconds.
     *  - sleep(1000) : 1.176 seconds.
     * threadCnt = 500;
     *  - non-sleep : 0.22 seconds.
     *  - sleep : 1.173 seconds.
     * */
    @Test
    @DisplayName("과도한 크기의 쓰레드풀 사용")
    void test02() {
        int threadCnt = 500;
        int tasks = 500;

        ExecutorService executor = Executors.newFixedThreadPool(threadCnt);

        Runnable task = () -> {
            long sum = 0;

            sleep(1000);

            for (long i = 0; i < 1000000L; i++) { sum += i; }
//            for (long i = 0; i < 100L; i++) { sum += i; }

            logger.info(Thread.currentThread().getName() + " finished.");
        };

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < tasks; i++) {
            executor.submit(task);
        }

        executor.shutdown();
        while (!executor.isTerminated()) {}

        long endTime = System.currentTimeMillis();
        logger.info("Excessive thread pool execution time: " + (endTime - startTime) / 1000.0 + " seconds.");
    }

    void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
