package com.study.etc.mask;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PersonalDataMasking 어노테이션 테스트")
class PersonalDataMaskingAnnotationTest {

    @ParameterizedTest
    @CsvSource({
        "defaultMethod, true",
        "disabledMethod, false"
    })
    @DisplayName("어노테이션 enabled 속성 확인")
    void 어노테이션_enabled_속성_확인(String methodName, boolean expectedEnabled) throws NoSuchMethodException {
        // given
        Method method = TestClass.class.getMethod(methodName);
        
        // when
        PersonalDataMasking annotation = method.getAnnotation(PersonalDataMasking.class);
        
        // then
        assertThat(annotation).isNotNull();
        assertThat(annotation.enabled()).isEqualTo(expectedEnabled);
    }

    @Test
    @DisplayName("어노테이션이 없는 메서드 확인")
    void 어노테이션_없는_메서드() throws NoSuchMethodException {
        // given
        Method method = TestClass.class.getMethod("noAnnotationMethod");
        
        // when
        PersonalDataMasking annotation = method.getAnnotation(PersonalDataMasking.class);
        
        // then
        assertThat(annotation).isNull();
    }

    @Test
    @DisplayName("어노테이션 타겟 확인")
    void 어노테이션_타겟_확인() {
        // given
        PersonalDataMasking annotation = TestClass.class.getAnnotation(PersonalDataMasking.class);
        
        // then - 클래스에는 적용할 수 없음
        assertThat(annotation).isNull();
    }

    // 테스트용 클래스
    public static class TestClass {
        
        @PersonalDataMasking
        public void defaultMethod() {}
        
        @PersonalDataMasking(enabled = false)
        public void disabledMethod() {}
        
        public void noAnnotationMethod() {}
    }
}
