package com.study.etc.reward;

import com.study.etc.reword.RewardMapper;
import com.study.etc.reword.RewardService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class RewardTest {

    Logger logger = LoggerFactory.getLogger(RewardTest.class);

    @Autowired
    private RewardService rewardService;
    @Autowired
    private RewardMapper rewardMapper;


    @Test
    void 비관적_락_보상_횟수_차감_성공() {
        // Given
        String groupType = "B";
        int initialCount = rewardService.getCurrentCountForPessimistic(groupType);

        // When
        rewardService.decrementPessimisticRewardCount(groupType);

        // Then
        int updatedCount = rewardService.getCurrentCountForPessimistic(groupType);
        assertThat(updatedCount).isEqualTo(initialCount - 1);
    }

    @Test
    void 낙관적_락_보상_횟수_차감_성공() {
        // Given
        String groupType = "C";
        int initialCount = rewardService.getCurrentCountForOptimistic(groupType);
        int version = rewardService.getCurrentVersion(groupType);

        // When
        rewardService.decrementOptimisticRewardCount(groupType);

        // Then
        int updatedCount = rewardService.getCurrentCountForOptimistic(groupType);
        int updatedVersion = rewardService.getCurrentVersion(groupType);
        assertThat(updatedCount).isEqualTo(initialCount - 1);
        assertThat(updatedVersion).isEqualTo(version + 1);
    }

    @Test
    void 낙관적_락_충돌_발생시_예외_발생() {
        // Given
        String groupType = "D";
        int version = rewardService.getCurrentVersion(groupType);

        // 이미 차감하여 버전 변경을 유도
        rewardService.decrementOptimisticRewardCount(groupType);

        // When & Then
        assertThatThrownBy(() -> rewardService.decrementOptimisticRewardCount(groupType))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("낙관적 락 충돌 발생");
    }

    @Test
    void 비관적_락_동시성_테스트() throws InterruptedException {
        // 비관적 락 테스트를 위해 초기화
        rewardService.resetPessimisticRewardCount();

        // Given
        String groupType = "D";

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {

//                    rewardService.decrementPessimisticRewardCount(groupType);

                    // 모든 스레드가 동시에 COUNT를 읽고, 업데이트 전 대기하여 경합 조건 유발
                    int countBeforeUpdate = rewardService.getCurrentCountForPessimistic(groupType);
                    System.out.println("스레드 " + Thread.currentThread().getName() + "에서 읽은 COUNT: " + countBeforeUpdate);

                    // 업데이트 전 대기
                    try {
                        Thread.sleep(100); // 100ms 대기하여 다른 스레드와 동시에 접근 유도
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    int updatedRows = rewardMapper.decrementPessimisticRewardCount(groupType);

                    if (updatedRows == 0) {
                        throw new RuntimeException("보상 횟수가 부족하여 차감할 수 없음");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
    }
}
