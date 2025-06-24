package com.study.etc.join.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordValidator {
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

    /**
     * 비밀번호가 유효한지 검증
     * - 2종류 조합 시 10자리 이상
     * - 3종류 이상 조합 시 8자리 이상
     */
    public boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }

        int typesCount = countCharacterTypes(password);

        if (typesCount < 2) {
            return false; // 최소 2종류 이상의 문자 조합이 필요
        } else if (typesCount == 2) {
            return password.length() >= 10; // 2종류 조합 시 10자리 이상
        } else { // 3종류 이상
            return password.length() >= 8; // 3종류 이상 조합 시 8자리 이상
        }
    }

    /**
     * 비밀번호에 포함된 문자 유형의 수를 계산
     */
    public int countCharacterTypes(String password) {
        int count = 0;

        if (UPPERCASE_PATTERN.matcher(password).find()) {
            count++;
        }

        if (LOWERCASE_PATTERN.matcher(password).find()) {
            count++;
        }

        if (DIGIT_PATTERN.matcher(password).find()) {
            count++;
        }

        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            count++;
        }

        return count;
    }
}
