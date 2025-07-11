package com.study.etc.mask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PersonalDataMaskingAdvice 개선된 테스트")
class PersonalDataMaskingAdviceTest {

    private PersonalDataMaskingAdvice advice;
    
    @Mock
    private MethodParameter methodParameter;
    
    @Mock
    private ServerHttpRequest request;
    
    @Mock
    private ServerHttpResponse response;
    
    @Mock
    private Class<? extends HttpMessageConverter<?>> converterType;

    @BeforeEach
    void setUp() {
        advice = new PersonalDataMaskingAdvice();
    }

    @Nested
    @DisplayName("필드 감지 로직 테스트")
    class FieldDetectionTest {

        @ParameterizedTest
        @ValueSource(strings = {"name", "userName", "nm", "성명", "username", "membername"})
        @DisplayName("이름 필드 감지")
        void isNameField_이름필드_감지(String fieldName) {
            assertThat(advice.isNameField(fieldName.toLowerCase())).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"email", "mail", "이메일", "userEmail", "adminMail"})
        @DisplayName("이메일 필드 감지")
        void isEmailField_이메일필드_감지(String fieldName) {
            assertThat(advice.isEmailField(fieldName.toLowerCase())).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"phone", "tel", "mobile", "전화", "휴대폰", "핸드폰", "phoneNumber"})
        @DisplayName("전화번호 필드 감지")
        void isPhoneField_전화번호필드_감지(String fieldName) {
            assertThat(advice.isPhoneField(fieldName.toLowerCase())).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"age", "address", "description", "title", "content"})
        @DisplayName("개인정보가 아닌 필드는 감지하지 않음")
        void 개인정보가_아닌_필드_미감지(String fieldName) {
            assertThat(advice.isNameField(fieldName.toLowerCase())).isFalse();
            assertThat(advice.isEmailField(fieldName.toLowerCase())).isFalse();
            assertThat(advice.isPhoneField(fieldName.toLowerCase())).isFalse();
        }
    }

    @Nested
    @DisplayName("객체 처리 로직 테스트")
    class ObjectProcessingTest {

        @Test
        @DisplayName("null 객체 처리")
        void processObject_null_처리() {
            Object result = advice.processObject(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("기본 타입 객체 처리")
        void processObject_기본타입_처리() {
            assertThat(advice.processObject("문자열")).isEqualTo("문자열");
            assertThat(advice.processObject(123)).isEqualTo(123);
            assertThat(advice.processObject(true)).isEqualTo(true);
        }

        @Test
        @DisplayName("빈 컬렉션 처리")
        void processObject_빈컬렉션_처리() {
            List<String> emptyList = Arrays.asList();
            Object result = advice.processObject(emptyList);
            
            assertThat(result).isSameAs(emptyList);
            assertThat((List<?>) result).isEmpty();
        }

        @Test
        @DisplayName("개인정보 객체 마스킹 처리")
        void processObject_개인정보객체_마스킹() {
            TestUser user = new TestUser("홍길동", "hong@example.com", "01012345678");
            
            Object result = advice.processObject(user);
            
            assertThat(result).isInstanceOf(TestUser.class);
            TestUser maskedUser = (TestUser) result;
            assertThat(maskedUser.getName()).isEqualTo("홍*동");
            assertThat(maskedUser.getEmail()).isEqualTo("ho***@example.com");
            assertThat(maskedUser.getPhone()).isEqualTo("010****5678");
        }

        @Test
        @DisplayName("중첩 객체 마스킹 처리")
        void processObject_중첩객체_마스킹() {
            TestUser innerUser = new TestUser("김철수", "kim@example.com", "01099998888");
            TestUserWrapper wrapper = new TestUserWrapper("관리자", innerUser);
            
            Object result = advice.processObject(wrapper);
            
            assertThat(result).isInstanceOf(TestUserWrapper.class);
            TestUserWrapper maskedWrapper = (TestUserWrapper) result;
            assertThat(maskedWrapper.getAdminName()).isEqualTo("관*자");
            assertThat(maskedWrapper.getUser().getName()).isEqualTo("김*수");
            assertThat(maskedWrapper.getUser().getEmail()).isEqualTo("ki***@example.com");
        }
    }

    @Nested
    @DisplayName("마스킹 품질 검증")
    class MaskingQualityTest {

        @ParameterizedTest
        @CsvSource({
            "홍길동, 홍*동",
            "김철수, 김*수", 
            "이영희, 이*희",
            "박민수, 박*수",
            "정, 정"
        })
        @DisplayName("이름 마스킹 품질 검증")
        void 이름_마스킹_품질_검증(String original, String expected) {
            TestUser user = new TestUser(original, "test@example.com", "010-1234-5678");
            
            Object result = advice.processObject(user);
            
            TestUser maskedUser = (TestUser) result;
            assertThat(maskedUser.getName()).isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({
            "test@example.com, te***@example.com",
            "a@gmail.com, *@gmail.com",
            "ab@test.com, **@test.com",
            "abc@naver.com, ***@naver.com",
            "abcd@gmail.com, ab***@gmail.com"
        })
        @DisplayName("이메일 마스킹 품질 검증")
        void 이메일_마스킹_품질_검증(String original, String expected) {
            TestUser user = new TestUser("테스트", original, "01012345678");
            
            Object result = advice.processObject(user);
            
            TestUser maskedUser = (TestUser) result;
            assertThat(maskedUser.getEmail()).isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({
            "01012345678, 010****5678",
            "01098765432, 010****5432",
            "0212345678, 02***5678",
            "123, 123"
        })
        @DisplayName("전화번호 마스킹 품질 검증")
        void 전화번호_마스킹_품질_검증(String original, String expected) {
            TestUser user = new TestUser("테스트", "test@example.com", original);
            
            Object result = advice.processObject(user);
            
            TestUser maskedUser = (TestUser) result;
            assertThat(maskedUser.getPhone()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("supports 메서드 테스트")
    class SupportsTest {

        @Test
        @DisplayName("@PersonalDataMasking 어노테이션이 있으면 true 반환")
        void supports_어노테이션_있음() throws NoSuchMethodException {
            Method method = TestController.class.getMethod("methodWithAnnotation");
            MethodParameter param = new MethodParameter(method, -1);
            
            boolean result = advice.supports(param, converterType);
            
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("@PersonalDataMasking 어노테이션이 없으면 false 반환")
        void supports_어노테이션_없음() throws NoSuchMethodException {
            Method method = TestController.class.getMethod("methodWithoutAnnotation");
            MethodParameter param = new MethodParameter(method, -1);
            
            boolean result = advice.supports(param, converterType);
            
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("beforeBodyWrite 메서드 테스트")
    class BeforeBodyWriteTest {

        @Test
        @DisplayName("어노테이션이 enabled=false면 원본 반환")
        void beforeBodyWrite_어노테이션_비활성화() throws NoSuchMethodException {
            Method method = TestController.class.getMethod("methodWithDisabledAnnotation");
            MethodParameter param = new MethodParameter(method, -1);
            TestUser originalUser = new TestUser("홍길동", "hong@gmail.com", "01012345678");
            
            Object result = advice.beforeBodyWrite(originalUser, param, MediaType.APPLICATION_JSON, 
                                                 converterType, request, response);
            
            assertThat(result).isSameAs(originalUser);
        }

        @Test
        @DisplayName("어노테이션이 있으면 마스킹 처리")
        void beforeBodyWrite_마스킹_처리() throws NoSuchMethodException {
            Method method = TestController.class.getMethod("methodWithAnnotation");
            MethodParameter param = new MethodParameter(method, -1);
            TestUser originalUser = new TestUser("홍길동", "hong123@gmail.com", "01012345678");
            
            Object result = advice.beforeBodyWrite(originalUser, param, MediaType.APPLICATION_JSON, 
                                                 converterType, request, response);
            
            assertThat(result).isNotSameAs(originalUser);
            TestUser maskedUser = (TestUser) result;
            assertThat(maskedUser.getName()).isEqualTo("홍*동");
            assertThat(maskedUser.getEmail()).isEqualTo("ho***@gmail.com");
            assertThat(maskedUser.getPhone()).isEqualTo("010****5678");
        }

        @Test
        @DisplayName("null 객체는 그대로 반환")
        void beforeBodyWrite_null_객체() throws NoSuchMethodException {
            Method method = TestController.class.getMethod("methodWithAnnotation");
            MethodParameter param = new MethodParameter(method, -1);
            
            Object result = advice.beforeBodyWrite(null, param, MediaType.APPLICATION_JSON, 
                                                 converterType, request, response);
            
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("예외 상황 처리 테스트")
    class ExceptionHandlingTest {

        @Test
        @DisplayName("직렬화/역직렬화 실패 시 원본 반환")
        void 직렬화_실패시_원본반환() {
            UnserializableObject unserializable = new UnserializableObject();
            
            Object result = advice.maskPersonalData(unserializable);
            
            assertThat(result).isSameAs(unserializable);
        }

        @Test
        @DisplayName("리플렉션 접근 실패 시 필드 건너뛰기")
        void 리플렉션_접근_실패시_건너뛰기() {
            TestUserWithPrivateFields user = new TestUserWithPrivateFields();
            
            assertThatNoException().isThrownBy(() -> {
                advice.processFields(user);
            });
        }
    }

    // 테스트용 클래스들
    public static class TestController {
        
        @PersonalDataMasking
        public TestUser methodWithAnnotation() {
            return null;
        }
        
        public TestUser methodWithoutAnnotation() {
            return null;
        }
        
        @PersonalDataMasking(enabled = false)
        public TestUser methodWithDisabledAnnotation() {
            return null;
        }
    }

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
    
    public static class TestUserWrapper {
        private String adminName;
        private TestUser user;
        
        public TestUserWrapper() {}
        
        public TestUserWrapper(String adminName, TestUser user) {
            this.adminName = adminName;
            this.user = user;
        }
        
        public String getAdminName() { return adminName; }
        public void setAdminName(String adminName) { this.adminName = adminName; }
        
        public TestUser getUser() { return user; }
        public void setUser(TestUser user) { this.user = user; }
    }

    public static class UnserializableObject {
        private final Thread thread = new Thread();
    }

    public static class TestUserWithPrivateFields {
        private final String finalField = "test";
        private static final String STATIC_FIELD = "static";
    }
}
