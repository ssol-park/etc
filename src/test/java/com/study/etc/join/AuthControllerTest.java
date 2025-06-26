package com.study.etc.join;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.etc.join.dto.UserRegistrationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    private static final Logger log = LoggerFactory.getLogger(AuthControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("유효한 사용자 등록 요청은 성공적으로 처리되어야 한다")
    @Test
    void shouldRegisterValidUser() throws Exception {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setName("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("Test1234!");

        // When & Then
        mockMvc.perform(post("/api/auth`")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @DisplayName("유효하지 않은 비밀번호는 등록 실패해야 한다")
    @Test
    void shouldFailWithInvalidPassword() throws Exception {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setName("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("weak");

        // When & Then
        mockMvc.perform(post("/api/uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    log.info("응답 본문: {}", responseBody);
                })
        ;
    }
}
