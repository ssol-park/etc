package com.study.etc.reword;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RewardService {

    private final RewardMapper rewardMapper;

    public RewardService(RewardMapper rewardMapper) {
        this.rewardMapper = rewardMapper;
    }

    @Transactional
    public void decrementPessimisticRewardCount(String groupType) {
        // 1. 잠금 설정: 다른 트랜잭션의 접근을 차단
        rewardMapper.lockForUpdate(groupType);

        // 2. 보상 횟수 차감
        int updatedRows = rewardMapper.decrementPessimisticRewardCount(groupType);

        if (updatedRows == 0) {
            throw new RuntimeException("보상 횟수가 부족하여 차감할 수 없음");
        }
    }

    @Transactional
    public void decrementOptimisticRewardCount(String groupType) {
        int currentVersion = rewardMapper.getCurrentVersion(groupType);
        int updatedRows = rewardMapper.decrementOptimisticRewardCount(groupType, currentVersion);

        if (updatedRows == 0) {
            throw new RuntimeException("낙관적 락 충돌 발생");
        }
    }

    // 현재 보상 횟수 조회 (비관적 락)
    public int getCurrentCountForPessimistic(String groupType) {
        return rewardMapper.getCurrentCountForPessimistic(groupType);
    }

    // 현재 보상 횟수 조회 (낙관적 락)
    public int getCurrentCountForOptimistic(String groupType) {
        return rewardMapper.getCurrentCountForOptimistic(groupType);
    }

    // 현재 버전 조회 (낙관적 락)
    public int getCurrentVersion(String groupType) {
        return rewardMapper.getCurrentVersion(groupType);
    }
}
