package com.study.etc.mask;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@DisplayName("마스킹 통합 테스트")
class MaskingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PersonalDataMaskingAdvice personalDataMaskingAdvice() {
            return new PersonalDataMaskingAdvice();
        }
    }

    @Test
    @DisplayName("마스킹 적용 API 테스트")
    void 마스킹_적용_API() throws Exception {
        MvcResult result = mockMvc.perform(get("/admin/accounts"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("Response Body: " + responseBody);
        
        // 응답이 비어있지 않은지 확인
        assertThat(responseBody).isNotEmpty();
        
        // 원본 데이터가 노출되지 않는지 확인 (이게 핵심)
        assertThat(responseBody).doesNotContain("홍길동");
        assertThat(responseBody).doesNotContain("hong123@gmail.com");
    }

    @Test
    @DisplayName("마스킹 비적용 API 테스트")
    void 마스킹_비적용_API() throws Exception {
        MvcResult result = mockMvc.perform(get("/admin/send-mail"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        
        // 원본 이메일 그대로 유지
        assertThat(responseBody).contains("hong123@gmail.com");
    }
}
