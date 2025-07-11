package com.study.etc.mask;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@DisplayName("마스킹 통합 테스트")
class MaskingIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Nested
    @DisplayName("실제 환경에서의 마스킹 동작 테스트")
    class RealEnvironmentTest {

        @Test
        @DisplayName("전체 애플리케이션 컨텍스트에서 마스킹 동작 확인")
        void 전체_컨텍스트_마스킹_동작_확인() throws Exception {
            MvcResult result = mockMvc.perform(get("/admin/accounts"))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> users = objectMapper.readValue(responseBody, List.class);
            
            assertThat(users).hasSize(3);
            
            Map<String, Object> firstUser = users.get(0);
            assertThat(firstUser.get("name")).isEqualTo("홍*동");
            assertThat(firstUser.get("email")).isEqualTo("ho***@gmail.com");
            assertThat(firstUser.get("phoneNumber")).isEqualTo("010****5678");
        }

        @Test
        @DisplayName("마스킹 비적용 엔드포인트 정상 동작 확인")
        void 마스킹_비적용_엔드포인트_정상동작() throws Exception {
            MvcResult result = mockMvc.perform(get("/admin/send-mail"))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            
            assertThat(responseBody).contains("hong123@gmail.com");
            assertThat(responseBody).doesNotContain("***");
        }
    }

    @Nested
    @DisplayName("예외 상황 처리 테스트")
    class ExceptionHandlingTest {

        @ParameterizedTest
        @CsvSource({
            "'', test@example.com, '', '', te***@example.com, ''",
            "홍길동, '', 01012345678, 홍*동, '', 010****5678",
            "'', '', '', '', '', ''"
        })
        @DisplayName("null/빈값이 포함된 객체 마스킹 처리")
        void null_빈값_포함_객체_마스킹(String name, String email, String phone,
                                    String expectedName, String expectedEmail, String expectedPhone) {
            PersonalDataMaskingAdvice advice = new PersonalDataMaskingAdvice();
            AdminController.UserDto user = new AdminController.UserDto(
                name.isEmpty() ? null : name,
                email.isEmpty() ? null : email,
                phone.isEmpty() ? null : phone
            );
            
            Object result = advice.maskPersonalData(user);
            
            assertThat(result).isNotNull();
            AdminController.UserDto maskedUser = (AdminController.UserDto) result;
            assertThat(maskedUser.getName()).isEqualTo(expectedName.isEmpty() ? null : expectedName);
            assertThat(maskedUser.getEmail()).isEqualTo(expectedEmail.isEmpty() ? null : expectedEmail);
            assertThat(maskedUser.getPhoneNumber()).isEqualTo(expectedPhone.isEmpty() ? null : expectedPhone);
        }

        @Test
        @DisplayName("빈 컬렉션 마스킹 처리")
        void 빈_컬렉션_마스킹() {
            PersonalDataMaskingAdvice advice = new PersonalDataMaskingAdvice();
            java.util.List<AdminController.UserDto> emptyList = new java.util.ArrayList<>();
            
            Object result = advice.maskPersonalData(emptyList);
            
            assertThat(result).isInstanceOf(java.util.List.class);
            @SuppressWarnings("unchecked")
            java.util.List<?> maskedList = (java.util.List<?>) result;
            assertThat(maskedList).isEmpty();
        }
    }
}
