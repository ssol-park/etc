package com.study.etc.mask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PersonalDataMaskingAdvice 단위 테스트")
class PersonalDataMaskingAdviceTest {

    private PersonalDataMaskingAdvice advice;

    @BeforeEach
    void setUp() {
        advice = new PersonalDataMaskingAdvice();
    }

    @Test
    @DisplayName("단일 객체 마스킹")
    void maskPersonalData_단일객체() {
        // given
        TestUser user = new TestUser("홍길동", "hong@test.com", "01012345678");
        ResponseDto<TestUser> responseDto = new ResponseDto<>(user);
        
        // when
        advice.maskPersonalData(responseDto);
        
        // then
        assertThat(responseDto.getData().getName()).isEqualTo("홍*동");
        assertThat(responseDto.getData().getEmail()).isEqualTo("ho***@test.com");
        assertThat(responseDto.getData().getPhone()).isEqualTo("010****5678");
    }

    @Test
    @DisplayName("리스트 객체 마스킹")
    void maskPersonalData_리스트() {
        // given
        List<TestUser> users = Arrays.asList(
            new TestUser("홍길동", "hong@test.com", "01012345678"),
            new TestUser("김철수", "kim@test.com", "01098765432")
        );
        ResponseDto<List<TestUser>> responseDto = new ResponseDto<>(users);
        
        // when
        advice.maskPersonalData(responseDto);
        
        // then
        List<TestUser> data = responseDto.getData();
        assertThat(data.get(0).getName()).isEqualTo("홍*동");
        assertThat(data.get(1).getName()).isEqualTo("김*수");
    }

    @Test
    @DisplayName("data 필드가 null인 경우")
    void maskPersonalData_data가_null() {
        // given
        ResponseDto<TestUser> responseDto = new ResponseDto<>(null);
        
        // when & then
        assertThatNoException().isThrownBy(() -> advice.maskPersonalData(responseDto));
    }

    // 테스트용 클래스들
    public static class TestUser {
        private String name;
        private String email;
        private String phone;
        
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
    
    public static class ResponseDto<T> {
        private T data;
        
        public ResponseDto(T data) {
            this.data = data;
        }
        
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }
}
