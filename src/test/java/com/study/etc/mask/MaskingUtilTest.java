package com.study.etc.mask;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MaskingUtil 단위 테스트")
class MaskingUtilTest {

    @ParameterizedTest
    @CsvSource({
        "홍길동, 홍*동",
        "김철수, 김*수",
        "이영희, 이*희",
        "홍, 홍"
    })
    @DisplayName("이름 마스킹")
    void maskName(String input, String expected) {
        assertThat(MaskingUtil.maskName(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        "a@test.com, *@test.com",
        "ab@test.com, **@test.com", 
        "abc@test.com, ***@test.com",
        "abcd@test.com, ab***@test.com",
        "hong123@gmail.com, ho***@gmail.com"
    })
    @DisplayName("이메일 마스킹")
    void maskEmail(String input, String expected) {
        assertThat(MaskingUtil.maskEmail(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        "01012345678, 010****5678",
        "0212345678, 021***5678",
        "031987654, 031**654"
    })
    @DisplayName("전화번호 마스킹")
    void maskPhoneNumber(String input, String expected) {
        assertThat(MaskingUtil.maskPhoneNumber(input)).isEqualTo(expected);
    }

    @Test
    @DisplayName("이메일 형식 검증")
    void isEmail() {
        assertThat(MaskingUtil.isEmail("test@example.com")).isTrue();
        assertThat(MaskingUtil.isEmail("invalid")).isFalse();
    }

    @Test
    @DisplayName("전화번호 형식 검증")
    void isPhoneNumber() {
        assertThat(MaskingUtil.isPhoneNumber("01012345678")).isTrue();
        assertThat(MaskingUtil.isPhoneNumber("123")).isFalse();
    }
}
