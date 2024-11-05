package com.study.etc.reward;

import com.study.etc.reword.RewardMapper;
import com.study.etc.reword.RewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class RewardTest {

    @Autowired
    private RewardService rewardService;

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
}
