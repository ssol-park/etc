package com.study.etc.join.controller;

import com.study.etc.join.dto.CommonResponse;
import com.study.etc.join.dto.UserRegistrationDto;
import com.study.etc.join.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    /**
     * UUID 생성 API
     *
     * @param userDto 유효성 검증이 필요한 사용자 등록 정보
     * @return UUID
     */
    @PostMapping("/uuid")
    public CommonResponse createUuid(@Valid @RequestBody UserRegistrationDto userDto) {
        return CommonResponse.success(authService.createUuid());
    }

}
