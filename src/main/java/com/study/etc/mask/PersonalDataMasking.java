package com.study.etc.mask;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 개인정보 마스킹 처리를 위한 어노테이션
 * 컨트롤러 메서드에 적용하여 응답 데이터의 개인정보를 마스킹합니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PersonalDataMasking {
    /**
     * 마스킹 적용 여부
     */
    boolean enabled() default true;
}
