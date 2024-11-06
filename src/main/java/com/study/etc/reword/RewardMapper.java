package com.study.etc.reword;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RewardMapper {
    void lockForUpdate(String groupType);
    int decrementPessimisticRewardCount(String groupType);
    int decrementOptimisticRewardCount(@Param("groupType") String groupType, @Param("version") int version);

    int getCurrentVersion(String groupType);

    int getCurrentCountForPessimistic(String groupType);
    int getCurrentCountForOptimistic(String groupType);

    void resetPessimisticRewardCount();

    void resetOptimisticRewardCount();
}
