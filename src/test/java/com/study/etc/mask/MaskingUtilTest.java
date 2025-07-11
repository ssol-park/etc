package com.study.etc.mask;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MaskingUtil 테스트")
class MaskingUtilTest {

    @Nested
    @DisplayName("이름 마스킹 테스트")
    class NameMaskingTest {

        @Test
        @DisplayName("정상적인 이름 마스킹 - 홍길동")
        void maskName_정상_홍길동() {
            // given
            String name = "홍길동";
            
            // when
            String result = MaskingUtil.maskName(name);
            
            // then
            assertThat(result).isEqualTo("홍*동");
        }

        @Test
        @DisplayName("정상적인 이름 마스킹 - 김철수")
        void maskName_정상_김철수() {
            // given
            String name = "김철수";
            
            // when
            String result = MaskingUtil.maskName(name);
            
            // then
            assertThat(result).isEqualTo("김*수");
        }

        @Test
        @DisplayName("4글자 이름 마스킹")
        void maskName_4글자_이름() {
            // given
            String name = "홍길동순";
            
            // when
            String result = MaskingUtil.maskName(name);
            
            // then
            assertThat(result).isEqualTo("홍*동*");
        }

        @Test
        @DisplayName("1글자 이름은 마스킹하지 않음")
        void maskName_1글자_이름() {
            // given
            String name = "홍";
            
            // when
            String result = MaskingUtil.maskName(name);
            
            // then
            assertThat(result).isEqualTo("홍");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null이나 빈 문자열은 그대로 반환")
        void maskName_null_또는_빈문자열(String name) {
            // when
            String result = MaskingUtil.maskName(name);
            
            // then
            assertThat(result).isEqualTo(name);
        }
    }

    @Nested
    @DisplayName("이메일 마스킹 테스트")
    class EmailMaskingTest {

        @ParameterizedTest
        @CsvSource({
            "a@example.com, *@example.com",
            "ab@gmail.com, **@gmail.com", 
            "abc@test.com, ***@test.com",
            "abcd@gmail.com, ab***@gmail.com",
            "test123@example.com, te***@example.com",
            "user.name@domain.co.kr, us***@domain.co.kr"
        })
        @DisplayName("정상적인 이메일 마스킹")
        void maskEmail_정상_케이스(String email, String expected) {
            // when
            String result = MaskingUtil.maskEmail(email);
            
            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("로컬 부분이 3글자 이하인 경우 글자 수만큼 * 처리")
        void maskEmail_3글자_이하_로컬부분() {
            // given & when & then
            assertThat(MaskingUtil.maskEmail("a@gmail.com")).isEqualTo("*@gmail.com");
            assertThat(MaskingUtil.maskEmail("ab@gmail.com")).isEqualTo("**@gmail.com");
            assertThat(MaskingUtil.maskEmail("abc@gmail.com")).isEqualTo("***@gmail.com");
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalid-email", "abc@", "@gmail.com", "abc@@gmail.com"})
        @DisplayName("잘못된 이메일 형식은 마스킹하지 않음")
        void maskEmail_잘못된_형식(String email) {
            // when
            String result = MaskingUtil.maskEmail(email);
            
            // then
            assertThat(result).isEqualTo(email);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null이나 빈 문자열은 그대로 반환")
        void maskEmail_null_또는_빈문자열(String email) {
            // when
            String result = MaskingUtil.maskEmail(email);
            
            // then
            assertThat(result).isEqualTo(email);
        }
    }

    @Nested
    @DisplayName("전화번호 마스킹 테스트")
    class PhoneNumberMaskingTest {

        @ParameterizedTest
        @CsvSource({
            "01012345678, 010****5678",
            "01098765432, 010****5432"
        })
        @DisplayName("11자리 전화번호 마스킹 (하이픈 없음)")
        void maskPhoneNumber_11자리_전화번호(String phoneNumber, String expected) {
            // when
            String result = MaskingUtil.maskPhoneNumber(phoneNumber);
            
            // then
            assertThat(result).isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({
            "0212345678, 021***5678",
            "0319876543, 031***6543"
        })
        @DisplayName("10자리 전화번호 마스킹 (하이픈 없음)")
        void maskPhoneNumber_10자리_전화번호(String phoneNumber, String expected) {
            // when
            String result = MaskingUtil.maskPhoneNumber(phoneNumber);
            
            // then
            assertThat(result).isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({
            "031987654, 031**654",
            "054123456, 054**456"
        })
        @DisplayName("9자리 전화번호 마스킹 (하이픈 없음)")
        void maskPhoneNumber_9자리_전화번호(String phoneNumber, String expected) {
            // when
            String result = MaskingUtil.maskPhoneNumber(phoneNumber);
            
            // then
            assertThat(result).isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({
            "010-1234-5678, 010****5678",
            "02-123-4567, 021**567",
            "031-987-6543, 031***6543"
        })
        @DisplayName("하이픈이 있는 전화번호도 하이픈 제거 후 마스킹")
        void maskPhoneNumber_하이픈있음_제거후_마스킹(String phoneNumber, String expected) {
            // when
            String result = MaskingUtil.maskPhoneNumber(phoneNumber);
            
            // then
            assertThat(result).isEqualTo(expected);
        }

        @ParameterizedTest
        @ValueSource(strings = {"123", "12345", "abc-def-ghij"})
        @DisplayName("8자리 미만이거나 잘못된 형식은 마스킹하지 않음")
        void maskPhoneNumber_잘못된_형식(String phoneNumber) {
            // when
            String result = MaskingUtil.maskPhoneNumber(phoneNumber);
            
            // then
            assertThat(result).isEqualTo(phoneNumber);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        @DisplayName("null이나 빈 문자열은 그대로 반환")
        void maskPhoneNumber_null_또는_빈문자열(String phoneNumber) {
            // when
            String result = MaskingUtil.maskPhoneNumber(phoneNumber);
            
            // then
            assertThat(result).isEqualTo(phoneNumber);
        }
    }

    @Nested
    @DisplayName("이메일 형식 검증 테스트")
    class EmailValidationTest {

        @ParameterizedTest
        @ValueSource(strings = {
            "test@example.com",
            "user.name@domain.co.kr",
            "user+tag@example.org",
            "user123@test-domain.com"
        })
        @DisplayName("유효한 이메일 형식")
        void isEmail_유효한_형식(String email) {
            // when & then
            assertThat(MaskingUtil.isEmail(email)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "invalid-email",
            "test@",
            "@example.com",
            "test@@example.com",
            "test@.com",
            "test@com"
        })
        @DisplayName("무효한 이메일 형식")
        void isEmail_무효한_형식(String email) {
            // when & then
            assertThat(MaskingUtil.isEmail(email)).isFalse();
        }

        @Test
        @DisplayName("null은 false 반환")
        void isEmail_null() {
            // when & then
            assertThat(MaskingUtil.isEmail(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("전화번호 형식 검증 테스트")
    class PhoneValidationTest {

        @ParameterizedTest
        @ValueSource(strings = {
            "010-1234-5678",
            "01012345678",
            "02-123-4567",
            "0212344567",
            "031-987-6543",
            "12345678"
        })
        @DisplayName("유효한 전화번호 형식")
        void isPhoneNumber_유효한_형식(String phoneNumber) {
            // when & then
            assertThat(MaskingUtil.isPhoneNumber(phoneNumber)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "123",
            "12345",
            "123456789012",
            "abc-def-ghij",
            "010-abcd-5678"
        })
        @DisplayName("무효한 전화번호 형식")
        void isPhoneNumber_무효한_형식(String phoneNumber) {
            // when & then
            assertThat(MaskingUtil.isPhoneNumber(phoneNumber)).isFalse();
        }

        @Test
        @DisplayName("null은 false 반환")
        void isPhoneNumber_null() {
            // when & then
            assertThat(MaskingUtil.isPhoneNumber(null)).isFalse();
        }
    }
}
