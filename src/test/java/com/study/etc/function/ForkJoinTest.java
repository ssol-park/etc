package com.study.etc.function;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;


class ForkJoinTest {
    // 임계값
    private static final int THRESHOLD = 1000;

    private static class ForkJoinSum extends RecursiveTask<Long> {
        private final long[] numbers;
        private final int start;
        private final int end;

        public ForkJoinSum(long[] numbers, int start, int end) {
            this.numbers = numbers;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            int length = end - start;
            // 현재 작업의 길이가 임계값 이하일 경우 직접 합을 계산
            if (length <= THRESHOLD) {
                return LongStream.range(start, end).map(i -> numbers[(int) i]).sum();
            } else {
                // 큰 작업을 두 개의 작은 작업으로 분할
                int middle = start + length / 2;
                ForkJoinSum leftTask = new ForkJoinSum(numbers, start, middle);
                ForkJoinSum rightTask = new ForkJoinSum(numbers, middle, end);

                // 왼쪽 작업을 비동기로 실행
                leftTask.fork();

                // 오른쪽 작업을 현재 스레드에서 계산
                long rightResult = rightTask.compute();

                // 왼쪽 작업 완료 대기
                long leftResult = leftTask.join();

                return leftResult + rightResult;
            }
        }
    }

    @Test
    public void testForkJoinSum() {
        long[] numbers = LongStream.rangeClosed(1, 10000).toArray();
        long expectedSum = LongStream.rangeClosed(1, 10000).sum();

        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinSum task = new ForkJoinSum(numbers, 0, numbers.length);
        long result = pool.invoke(task);

        assertThat(expectedSum).isEqualTo(result);
    }
}
