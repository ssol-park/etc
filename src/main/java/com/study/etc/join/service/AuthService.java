package com.study.etc.join.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class AuthService {

    /**
     * UUID 생성
     *
     * @return 사용자 UUID
     */
    public String createUuid() {
        return UUID.randomUUID().toString();
    }

}
