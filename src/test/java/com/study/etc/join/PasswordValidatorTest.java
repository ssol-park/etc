package com.study.etc.join;

import com.study.etc.join.util.PasswordValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordValidatorTest {

    private PasswordValidator validator = new PasswordValidator();

    @DisplayName("유효한 비밀번호는 검증에 통과해야 한다")
    @Test
    void shouldValidateCorrectPassword() {
        String validPassword = "Test1234!";

        boolean result = validator.isValidPassword(validPassword);

        assertThat(result).isTrue();
    }


}
