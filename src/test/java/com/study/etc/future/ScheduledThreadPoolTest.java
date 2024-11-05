package com.study.etc.future;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class ScheduledThreadPoolTest {
    Logger logger = LoggerFactory.getLogger(ScheduledThreadPoolTest.class);
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    ExecutorService executor = Executors.newFixedThreadPool(10);


    <T> CompletableFuture<T> withTimeout(CompletableFuture<T> future, long timeout, TimeUnit unit) {
        final CompletableFuture<T> timeoutFuture = new CompletableFuture<>();

        scheduler.schedule(() -> {
            timeoutFuture.completeExceptionally(new TimeoutException());
        }, timeout, unit);

        return future.applyToEither(timeoutFuture, Function.identity());
    }

    @Test
    void testScheduledThread() {

        List<CompletableFuture<Integer>> futures = IntStream.rangeClosed(1, 100)
                .mapToObj(taskNumber -> {
                    CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                        logger.info("Task {} started", taskNumber);

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            throw new IllegalStateException(e);
                        }

                        logger.info("Task {} completed", taskNumber);

                        return taskNumber;

                    }, executor);

                    return withTimeout(future, 10, TimeUnit.SECONDS);
                })
                .collect(Collectors.toList());

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();

//        futures.forEach(future -> logger.info("완료 :: {}", future.join()));
    }
}
