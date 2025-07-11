package com.study.etc.mask;

import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * 마스킹 기능 전체 테스트 슈트
 * 모든 마스킹 관련 테스트를 한 번에 실행할 수 있습니다.
 */
@Suite
@SelectClasses({
    MaskingUtilTest.class,
    PersonalDataMaskingAdviceTest.class,
    PersonalDataMaskingAnnotationTest.class,
    AdminControllerTest.class,
    MaskingIntegrationTest.class
})
@DisplayName("개인정보 마스킹 기능 전체 테스트")
public class MaskingTestSuite {
    // 테스트 슈트 실행용 클래스
    // 모든 마스킹 관련 테스트를 한 번에 실행합니다.
}
