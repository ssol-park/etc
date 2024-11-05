package com.study.etc.future;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class ForkJoinPoolTest {

    Logger logger = LoggerFactory.getLogger(ForkJoinPoolTest.class);

    @Test
    void commonPoolTest() {
        logger.info("[commonPoolTest] ===================== ");

        // CPU 의 코어 수 만큼 ForkJoinPool 이 생성됨
        IntStream.rangeClosed(1, 1000).parallel()
                        .mapToObj(n -> {
                            try {
                                logger.info("Sleep Start....");
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            return n * n;
                        })
                .forEach(r -> logger.info("{}", r));
    }

    @Test
    void commonPoolTest2() {

        List<CompletableFuture<Integer>> futures = IntStream.rangeClosed(1, 10).parallel()
                .mapToObj(n -> {
                    try {
                        if (n % 6 == 0) {
                            logger.info("Deep sleep ================ {}", n);
                            Thread.sleep(10000);
                        } else {
                            logger.info("Running :: {}", n);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    return CompletableFuture.supplyAsync(() -> {
                        int num = n * n;
                        logger.info("[CompletableFuture] Return :: {}", num);
                        return num;
                    });
                })
                .collect(Collectors.toList());

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        allOf.thenRun(() -> {
            futures.forEach(future -> {
                int num = future.join();
                logger.info("future :: {}", num);
            });
        }).join();
    }
}
