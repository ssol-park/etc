package com.study.etc.mask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("마스킹 성능 테스트")
class MaskingPerformanceTest {

    private PersonalDataMaskingAdvice advice;

    @BeforeEach
    void setUp() {
        advice = new PersonalDataMaskingAdvice();
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000, 5000})
    @DisplayName("데이터 크기별 마스킹 성능 측정")
    void 데이터_크기별_마스킹_성능_측정(int dataSize) {
        // given
        List<TestUser> users = createTestUsers(dataSize);
        StopWatch stopWatch = new StopWatch("마스킹 성능 테스트");
        
        // when
        stopWatch.start("데이터 크기: " + dataSize);
        Object result = advice.maskPersonalData(users);
        stopWatch.stop();
        
        // then
        long executionTimeMs = stopWatch.getLastTaskTimeMillis();
        
        // 성능 기준: 1000개당 100ms 이하
        long expectedMaxTime = (dataSize / 1000 + 1) * 100;
        assertThat(executionTimeMs)
            .as("데이터 %d개 처리 시간이 %dms를 초과함", dataSize, expectedMaxTime)
            .isLessThanOrEqualTo(expectedMaxTime);
        
        // 결과 검증
        assertThat(result).isNotNull();
        @SuppressWarnings("unchecked")
        List<TestUser> maskedUsers = (List<TestUser>) result;
        assertThat(maskedUsers).hasSize(dataSize);
        
        System.out.printf("데이터 %d개 처리 시간: %dms%n", dataSize, executionTimeMs);
    }

    @Test
    @DisplayName("대용량 데이터 처리 안정성 테스트")
    void 대용량_데이터_처리_안정성() {
        // given - 10,000개의 사용자 데이터
        List<TestUser> largeDataSet = createTestUsers(10_000);
        
        // when & then - 메모리 부족이나 스택 오버플로우 없이 처리되어야 함
        assertThatNoException()
            .as("대용량 데이터 처리 중 예외 발생")
            .isThrownBy(() -> {
                advice.maskPersonalData(largeDataSet);
            });
    }

    private List<TestUser> createTestUsers(int count) {
        List<TestUser> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(new TestUser(
                "사용자" + i,
                "user" + i + "@example.com",
                "010" + String.format("%08d", i % 100000000)
            ));
        }
        return users;
    }

    // 테스트용 클래스
    public static class TestUser {
        private String name;
        private String email;
        private String phone;
        
        public TestUser() {}
        
        public TestUser(String name, String email, String phone) {
            this.name = name;
            this.email = email;
            this.phone = phone;
        }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}
