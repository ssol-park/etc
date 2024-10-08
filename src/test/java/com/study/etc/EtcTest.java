package com.study.etc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest
class EtcTest {

    @Test
    void testThreadAsync() {
        // 메인 함수에서 비동기 작업의 결과를 기다림 (에러 발생 시에도 리턴 받음)
        CompletableFuture<String> future = runAsyncTask();

        // 메인 스레드에서 비동기 작업의 결과를 기다림
        String finalResult = future.handle((result, ex) -> {
            if (ex != null) {
                System.out.println("* [5] :: Error occurred in main: " + ex.getMessage() + " in thread: " + Thread.currentThread().getName());
                return "Handled failure in main";
            } else {
                return result;
            }
        }).join();

        // 최종 결과 출력
        System.out.println("* [6] :: Final result in main: " + finalResult + " in thread: " + Thread.currentThread().getName());
    }

    // 비동기 작업을 수행하는 함수
    CompletableFuture<String> runAsyncTask() {
        CompletableFuture<String> future = new CompletableFuture<>();

        // 비동기 작업 수행
        new Thread(() -> {
            try {
                System.out.println("* [1] :: Thread running the async task: " + Thread.currentThread().getName());

                // 작업 중...
                await().atMost(1, TimeUnit.SECONDS).until(doSomething());

                future.complete("Success!");
            } catch (Exception e) {
                future.completeExceptionally(e);  // 예외 발생 시
            }
        }).start();

        // 성공 시 결과 처리
        future
                .handle((result, ex) -> {
                    if (ex != null) {
                        System.out.println("* [2] :: Error occurred: " + ex.getMessage() + " in thread: " + Thread.currentThread().getName());
                        return "Handled failure";
                    } else {
                        System.out.println("* [2] :: Handling result in thread: " + Thread.currentThread().getName());
                        return result;
                    }
                })
                .thenAccept(result -> {
                    System.out.println("* [3] :: Final result: " + result + " in thread: " + Thread.currentThread().getName());
                })
                .exceptionally(ex -> {
                    System.out.println("* [4] :: Exceptionally handling error: " + ex.getMessage() + " in thread: " + Thread.currentThread().getName());
                    return null;
                });

        return future;  // 비동기 작업 결과를 반환
    }

    Callable<Boolean> doSomething() {
        return () -> {
            if (Math.random() > 0.5) {
                throw new RuntimeException("Task failed");
            }

            return true;
        };
    }
}
