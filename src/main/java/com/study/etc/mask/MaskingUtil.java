package com.study.etc.mask;

import java.util.regex.Pattern;

/**
 * 개인정보 마스킹 처리 유틸리티 클래스
 */
public class MaskingUtil {
    
    // 정규식 패턴들을 미리 컴파일하여 성능 향상
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern DIGITS_ONLY_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern PHONE_VALIDATION_PATTERN = Pattern.compile("^\\d{8,11}$");
    private static final Pattern HYPHEN_PATTERN = Pattern.compile("-");
    
    /**
     * 이름 마스킹 처리 (짝수 자리 마스킹)
     * 예: "홍길동" -> "홍*동", "김철수" -> "김*수"
     */
    public static String maskName(String name) {
        if (name == null || name.length() <= 1) {
            return name;
        }
        
        // 초기 용량을 name 길이로 설정하여 재할당 방지
        StringBuilder masked = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); i++) {
            if (i % 2 == 1) { // 짝수 자리 (0-based이므로 1,3,5... 인덱스)
                masked.append('*');
            } else {
                masked.append(name.charAt(i));
            }
        }
        return masked.toString();
    }
    
    /**
     * 이메일 마스킹 처리
     * 로컬 부분이 3개 이하면 글자 수만큼 * 처리, 4개 이상이면 처음 2자리만 보여주고 나머지는 ***
     * 예: "a@gmail.com" -> "*@gmail.com", "ab@gmail.com" -> "**@gmail.com", "abc@gmail.com" -> "***@gmail.com", "abcd@gmail.com" -> "ab***@gmail.com"
     */
    public static String maskEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            return email;
        }
        
        int atIndex = email.indexOf('@');
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);
        
        StringBuilder result = new StringBuilder(email.length());
        
        if (localPart.length() <= 3) {
            // 3글자 이하면 글자 수만큼 * 처리
            appendRepeatedChar(result, '*', localPart.length());
        } else {
            // 4글자 이상이면 처음 2자리만 보여주고 나머지는 ***
            result.append(localPart, 0, 2).append("***");
        }
        result.append(domainPart);
        
        return result.toString();
    }
    
    /**
     * 전화번호 마스킹 처리 (하이픈 없음)
     * 예: "01012345678" -> "010****5678"
     * 예: "0212345678" -> "021***5678"
     * 예: "031987654" -> "031**654"
     */
    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        
        // trim() 최적화: 앞뒤 공백이 있는 경우에만 trim 수행
        String trimmedPhone = phoneNumber;
        if (needsTrim(phoneNumber)) {
            trimmedPhone = phoneNumber.trim();
            if (trimmedPhone.isEmpty()) {
                return phoneNumber;
            }
        } else if (phoneNumber.isEmpty()) {
            return phoneNumber;
        }
        
        // 하이픈이 있는 경우에만 제거
        String cleanPhone = trimmedPhone;
        if (trimmedPhone.indexOf('-') >= 0) {
            cleanPhone = HYPHEN_PATTERN.matcher(trimmedPhone).replaceAll("");
        }
        
        if (cleanPhone.length() < 8) {
            return phoneNumber; // 너무 짧으면 마스킹하지 않음
        }
        
        // 숫자가 아닌 문자가 포함되어 있으면 마스킹하지 않음
        if (!DIGITS_ONLY_PATTERN.matcher(cleanPhone).matches()) {
            return phoneNumber;
        }
        
        return applyPhoneMasking(cleanPhone);
    }
    
    /**
     * 전화번호 길이에 따른 마스킹 적용
     */
    private static String applyPhoneMasking(String cleanPhone) {
        StringBuilder result = new StringBuilder(cleanPhone.length());
        
        switch (cleanPhone.length()) {
            case 11:
                if (cleanPhone.startsWith("010")) {
                    return result.append(cleanPhone, 0, 3)
                            .append("****")
                            .append(cleanPhone, 7, 11)
                            .toString();
                }
                break;
            case 10:
                return result.append(cleanPhone, 0, 3)
                        .append("***")
                        .append(cleanPhone, 6, 10)
                        .toString();
            case 9:
                return result.append(cleanPhone, 0, 3)
                        .append("**")
                        .append(cleanPhone, 6, 9)
                        .toString();
        }
        
        return cleanPhone; // 예상하지 못한 형태는 마스킹하지 않음
    }
    
    /**
     * 문자열이 이메일 형식인지 확인
     */
    public static boolean isEmail(String value) {
        return value != null && EMAIL_PATTERN.matcher(value).matches();
    }
    
    /**
     * 문자열이 전화번호 형식인지 확인
     */
    public static boolean isPhoneNumber(String value) {
        if (value == null) {
            return false;
        }
        
        String cleanValue = value;
        if (value.indexOf('-') >= 0) {
            cleanValue = HYPHEN_PATTERN.matcher(value).replaceAll("");
        }
        
        return PHONE_VALIDATION_PATTERN.matcher(cleanValue).matches();
    }
    
    /**
     * 문자 반복을 StringBuilder에 추가 (String.repeat 대신 사용)
     */
    private static void appendRepeatedChar(StringBuilder sb, char ch, int count) {
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
    }
    
    /**
     * 문자열이 trim이 필요한지 확인
     */
    private static boolean needsTrim(String str) {
        if (str.isEmpty()) {
            return false;
        }
        return Character.isWhitespace(str.charAt(0)) || 
               Character.isWhitespace(str.charAt(str.length() - 1));
    }
}
