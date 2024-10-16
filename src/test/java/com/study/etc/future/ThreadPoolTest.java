package com.study.etc.future;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

class ThreadPoolTest {
    Logger logger = LoggerFactory.getLogger(ThreadPoolTest.class);

    @Test
    void testThreadPoolExecutor() {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(); // capacity 설정 가능

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
          10,
          100,
          60L,
          TimeUnit.SECONDS,
          queue
        );


        List<CompletableFuture<Void>> completableFutures = IntStream.rangeClosed(1, 200)
                .mapToObj(i ->
                        CompletableFuture.runAsync(() -> {
                            logger.info("IDX:{} QueueSize:{} ActiveThreads:{}", i, queue.size(), threadPool.getActiveCount());

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }, threadPool)
                )
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));

        allOf.join();
    }
}
