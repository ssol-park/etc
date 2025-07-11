package com.study.etc.mask;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AdminController.class)
@DisplayName("AdminController 개선된 테스트")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PersonalDataMaskingAdvice personalDataMaskingAdvice() {
            return new PersonalDataMaskingAdvice();
        }
    }

    @ParameterizedTest
    @MethodSource("마스킹_엔드포인트_데이터")
    @DisplayName("마스킹 적용 엔드포인트별 검증")
    void 마스킹_적용_엔드포인트_검증(String endpoint, String[] expectedMaskedData, String[] shouldNotContain) throws Exception {
        MvcResult result = mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        
        // 마스킹된 데이터 확인
        for (String maskedData : expectedMaskedData) {
            assertThat(responseBody).contains(maskedData);
        }
        
        // 원본 데이터가 노출되지 않았는지 확인
        for (String originalData : shouldNotContain) {
            assertThat(responseBody).doesNotContain(originalData);
        }
    }

    static Stream<Arguments> 마스킹_엔드포인트_데이터() {
        return Stream.of(
            Arguments.of(
                "/admin/accounts",
                new String[]{"홍*동", "ho***@gmail.com", "010****5678", "김*수", "이*희"},
                new String[]{"홍길동", "김철수", "이영희", "hong123@gmail.com", "kimcs456@naver.com"}
            ),
            Arguments.of(
                "/admin/mail-history", 
                new String[]{"홍*동", "ho***@gmail.com", "김*수", "ki***@naver.com"},
                new String[]{"홍길동", "김철수", "hong123@gmail.com", "kimcs456@naver.com"}
            )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"홍길동", "김철수", "이영희", "hong123@gmail.com", "kimcs456@naver.com", "lee789@daum.net"})
    @DisplayName("마스킹 적용 페이지에서 원본 개인정보 완전 차단")
    void 마스킹_페이지_원본데이터_완전차단(String sensitiveData) throws Exception {
        MvcResult accountsResult = mockMvc.perform(get("/admin/accounts"))
                .andExpect(status().isOk())
                .andReturn();
        
        MvcResult mailHistoryResult = mockMvc.perform(get("/admin/mail-history"))
                .andExpect(status().isOk())
                .andReturn();

        // 모든 마스킹 적용 페이지에서 민감 데이터가 노출되지 않아야 함
        assertThat(accountsResult.getResponse().getContentAsString())
            .as("계정 관리 페이지에서 %s가 노출됨", sensitiveData)
            .doesNotContain(sensitiveData);
            
        assertThat(mailHistoryResult.getResponse().getContentAsString())
            .as("메일 이력 페이지에서 %s가 노출됨", sensitiveData)
            .doesNotContain(sensitiveData);
    }

    @Test
    @DisplayName("마스킹 비적용 엔드포인트 - 원본 데이터 유지")
    void 마스킹_비적용_엔드포인트_원본유지() throws Exception {
        MvcResult result = mockMvc.perform(get("/admin/send-mail"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        
        // 원본 이메일이 그대로 노출되어야 함 (비즈니스 로직 정상 동작)
        assertThat(responseBody).contains("hong123@gmail.com");
        // 마스킹 문자가 없어야 함
        assertThat(responseBody).doesNotContain("***");
    }

    @Test
    @DisplayName("응답 JSON 구조 및 데이터 무결성 검증")
    void 응답_JSON_구조_및_데이터_무결성_검증() throws Exception {
        MvcResult result = mockMvc.perform(get("/admin/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        
        // JSON 파싱 성공 여부 확인
        AdminController.UserDto[] users = objectMapper.readValue(responseBody, AdminController.UserDto[].class);
        assertThat(users).hasSize(3);
        
        // 각 사용자 데이터의 마스킹 품질 검증
        AdminController.UserDto firstUser = users[0];
        assertThat(firstUser.getName())
            .as("첫 번째 사용자 이름 마스킹")
            .isEqualTo("홍*동");
        assertThat(firstUser.getEmail())
            .as("첫 번째 사용자 이메일 마스킹")
            .isEqualTo("ho***@gmail.com");
        assertThat(firstUser.getPhoneNumber())
            .as("첫 번째 사용자 전화번호 마스킹")
            .isEqualTo("010****5678");
            
        // 모든 사용자 데이터가 마스킹되었는지 확인
        for (AdminController.UserDto user : users) {
            assertThat(user.getName()).contains("*");
            assertThat(user.getEmail()).contains("***");
            assertThat(user.getPhoneNumber()).contains("*");
        }
    }

    @Test
    @DisplayName("메일 이력 페이지 - 마스킹과 비마스킹 필드 구분")
    void 메일이력_마스킹_비마스킹_필드_구분() throws Exception {
        MvcResult result = mockMvc.perform(get("/admin/mail-history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        
        // 개인정보는 마스킹되어야 함
        assertThat(responseBody).contains("홍*동");
        assertThat(responseBody).contains("ho***@gmail.com");
        
        // 비즈니스 데이터는 마스킹되지 않아야 함
        assertThat(responseBody).contains("회원가입 인증");
        assertThat(responseBody).contains("비밀번호 재설정");
        assertThat(responseBody).contains("2024-01-15 10:30:00");
        assertThat(responseBody).contains("2024-01-15 11:15:00");
        
        // 메일 제목과 날짜에는 마스킹 문자가 없어야 함
        AdminController.MailHistoryDto[] mailHistory = objectMapper.readValue(responseBody, AdminController.MailHistoryDto[].class);
        for (AdminController.MailHistoryDto mail : mailHistory) {
            assertThat(mail.getSubject()).doesNotContain("*");
            assertThat(mail.getSentDate()).doesNotContain("*");
        }
    }

    @Test
    @DisplayName("HTTP 상태 코드 및 Content-Type 검증")
    void HTTP_상태코드_및_ContentType_검증() throws Exception {
        mockMvc.perform(get("/admin/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().string("Content-Type", "application/json"));
                
        mockMvc.perform(get("/admin/mail-history"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
                
        mockMvc.perform(get("/admin/send-mail"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/plain"));
    }
}
