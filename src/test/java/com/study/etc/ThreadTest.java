package com.study.etc;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ThreadTest {

    private static final Logger logger = LoggerFactory.getLogger(ThreadTest.class);

    @Test
    void testRunMultipleAsyncTasks() {
        // 스레드 풀 생성 (고정 크기)
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        // 비동기 작업의 개수 설정
        int numberOfTasks = 5;

        // 비동기 작업 수행
        List<CompletableFuture<String>> futures = runMultipleAsyncTasks(threadPool, numberOfTasks);

        // CompletableFuture.allOf()로 모든 작업이 완료될 때까지 대기
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        // 30초 이내에 모든 작업이 완료되지 않으면 타임아웃 처리
        try {
            allFutures.get(30, TimeUnit.SECONDS);
            logger.info("[Test] :: All tasks completed successfully within the timeout.");
        } catch (Exception e) {
            logger.error("[Test] :: Some tasks did not finish within 30 seconds: {}", e.getMessage());
        }

        // 각 작업의 결과를 검증
        for (int i = 0; i < futures.size(); i++) {
            try {
                String result = futures.get(i).join();  // 이미 예외가 처리됨
                logger.info("[Test] :: Task {} result: {}", (i + 1), result);

                assertThat(result).contains("Success!", "Timeout occurred", "Unexpected error");

            } catch (Exception e) {
                logger.error("[Test] :: Exception during task {} completion: {}", (i + 1), e.getMessage());
            }
        }

        // 메인 스레드에서 최종 완료 메시지 출력
        logger.info("[Test] :: All tasks finished in test thread: {}", Thread.currentThread().getName());

        // 스레드 풀 종료
        threadPool.shutdown();
    }

    // 비동기 작업을 동적으로 여러 개 생성하는 함수
    private List<CompletableFuture<String>> runMultipleAsyncTasks(ExecutorService threadPool, int numberOfTasks) {
        return IntStream.rangeClosed(1, numberOfTasks)
                .mapToObj(taskNumber -> runAsyncTask(threadPool, 5000, taskNumber)
                        .exceptionally(ex -> String.format("Task %d :: %s", taskNumber, ex.getMessage()))
                ).toList();
    }

    // 비동기 작업을 수행하는 함수
    private CompletableFuture<String> runAsyncTask(ExecutorService threadPool, long timeoutInMillis, int taskNumber) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("[Async] :: Thread running the async task: {}", Thread.currentThread().getName());

                if (taskNumber % 2 == 0) {
                    // 작업 중...
                    logger.warn("[Delay] taskNumber: {}", taskNumber);
                    Thread.sleep(6000);  // 작업 수행
                }

                if (Math.random() > 0.5) {
                    throw new RuntimeException("Error task : " + taskNumber);
                }
                return "Success";

            } catch (Exception e) {
                throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
            }
        }, threadPool)
        .completeOnTimeout("Timeout occurred!", timeoutInMillis, TimeUnit.MILLISECONDS)
        .whenComplete((result, e) -> {
            if (e != null) 
                logger.error("[Async] :: Task failed {}", e.getMessage());
            else
                logger.info("[Async] :: Task {}", result);
        });
    }
}
