package com.study.etc.join.annotation;

import com.study.etc.join.util.PasswordValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public class PasswordPolicyValidator implements ConstraintValidator<PasswordPolicy, String> {

    private final PasswordValidator passwordValidator;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        if (value == null) {
            return true;
        }

        return passwordValidator.isValidPassword(value);

    }
}
