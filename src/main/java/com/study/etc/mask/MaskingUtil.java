package com.study.etc.mask;

import java.util.regex.Pattern;

/**
 * 개인정보 마스킹 처리 유틸리티 클래스
 */
public class MaskingUtil {
    
    // 이메일 패턴
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    /**
     * 이름 마스킹 처리 (짝수 자리 마스킹)
     * 예: "홍길동" -> "홍*동", "김철수" -> "김*수"
     */
    public static String maskName(String name) {
        if (name == null || name.length() <= 1) {
            return name;
        }
        
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            if (i % 2 == 1) { // 짝수 자리 (0-based이므로 1,3,5... 인덱스)
                masked.append("*");
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
        
        int atIndex = email.indexOf("@");
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);
        
        String maskedLocal;
        if (localPart.length() <= 3) {
            // 3글자 이하면 글자 수만큼 * 처리
            maskedLocal = "*".repeat(localPart.length());
        } else {
            // 4글자 이상이면 처음 2자리만 보여주고 나머지는 ***
            maskedLocal = localPart.substring(0, 2) + "***";
        }
        
        return maskedLocal + domainPart;
    }
    
    /**
     * 전화번호 마스킹 처리 (하이픈 없음)
     * 예: "01012345678" -> "010****5678"
     * 예: "0212345678" -> "02***5678"
     * 예: "031987654" -> "031**654"
     */
    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return phoneNumber;
        }
        
        // 숫자만 추출 (하이픈이 있어도 제거)
        String cleanPhone = phoneNumber.replaceAll("-", "");
        
        if (cleanPhone.length() < 8) {
            return phoneNumber; // 너무 짧으면 마스킹하지 않음
        }
        
        // 숫자가 아닌 문자가 포함되어 있으면 마스킹하지 않음
        if (!cleanPhone.matches("^\\d+$")) {
            return phoneNumber;
        }
        
        // 11자리 (010-XXXX-XXXX 형태)
        if (cleanPhone.length() == 11 && cleanPhone.startsWith("010")) {
            return cleanPhone.substring(0, 3) + "****" + cleanPhone.substring(7);
        } 
        // 10자리 (031-XXX-XXXX 등)
        else if (cleanPhone.length() == 10) {
            return cleanPhone.substring(0, 3) + "***" + cleanPhone.substring(6);
        }
        // 9자리 (031-XX-XXXX 등)
        else if (cleanPhone.length() == 9) {
            return cleanPhone.substring(0, 3) + "**" + cleanPhone.substring(6);
        }
        
        return phoneNumber; // 예상하지 못한 형태는 마스킹하지 않음
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
        if (value == null) return false;
        String cleanValue = value.replaceAll("-", "");
        return cleanValue.matches("^\\d{8,11}$");
    }
}
