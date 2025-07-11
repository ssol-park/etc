package com.study.etc.mask;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 개인정보 마스킹 처리를 위한 ResponseBodyAdvice
 * @PersonalDataMasking 어노테이션이 붙은 컨트롤러 메서드의 응답을 마스킹 처리합니다.
 */
@ControllerAdvice
public class PersonalDataMaskingAdvice implements ResponseBodyAdvice<Object> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // @PersonalDataMasking 어노테이션이 있는 메서드만 처리
        return returnType.hasMethodAnnotation(PersonalDataMasking.class);
    }
    
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request, ServerHttpResponse response) {
        
        PersonalDataMasking annotation = returnType.getMethodAnnotation(PersonalDataMasking.class);
        if (annotation == null || !annotation.enabled()) {
            return body;
        }
        
        return maskPersonalData(body);
    }
    
    /**
     * 객체의 개인정보를 마스킹 처리
     * 테스트를 위해 package-private으로 변경
     */
    Object maskPersonalData(Object obj) {
        if (obj == null) {
            return null;
        }
        
        try {
            // 원본 객체를 JSON으로 변환 후 다시 객체로 변환 (깊은 복사)
            String json = objectMapper.writeValueAsString(obj);
            Object copiedObj = objectMapper.readValue(json, obj.getClass());
            
            // 마스킹 처리
            return processObject(copiedObj);
            
        } catch (Exception e) {
            // 예외 발생 시 원본 반환
            return obj;
        }
    }
    
    /**
     * 객체를 재귀적으로 처리하여 개인정보 필드를 마스킹
     * 테스트를 위해 package-private으로 변경
     */
    Object processObject(Object obj) {
        if (obj == null) {
            return null;
        }
        
        // 기본 타입이나 String인 경우 그대로 반환
        if (isPrimitiveOrWrapper(obj.getClass()) || obj instanceof String) {
            return obj;
        }
        
        // Collection 처리
        if (obj instanceof Collection) {
            Collection<?> collection = (Collection<?>) obj;
            collection.forEach(this::processObject);
            return obj;
        }
        
        // Map 처리
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            map.values().forEach(this::processObject);
            return obj;
        }
        
        // 일반 객체 처리
        processFields(obj);
        return obj;
    }
    
    /**
     * 객체의 필드들을 검사하여 개인정보로 추정되는 필드를 마스킹
     * 테스트를 위해 package-private으로 변경
     */
    void processFields(Object obj) {
        Class<?> clazz = obj.getClass();
        
        // 패키지가 java. 또는 javax. 로 시작하는 클래스는 처리하지 않음
        if (clazz.getName().startsWith("java.") || clazz.getName().startsWith("javax.")) {
            return;
        }
        
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(obj);
                
                if (value == null) {
                    continue;
                }
                
                // String 타입 필드 검사
                if (value instanceof String) {
                    String stringValue = (String) value;
                    String fieldName = field.getName().toLowerCase();
                    
                    // 필드명 기반 마스킹
                    if (isNameField(fieldName)) {
                        field.set(obj, MaskingUtil.maskName(stringValue));
                    } else if (isEmailField(fieldName) || MaskingUtil.isEmail(stringValue)) {
                        field.set(obj, MaskingUtil.maskEmail(stringValue));
                    } else if (isPhoneField(fieldName) || MaskingUtil.isPhoneNumber(stringValue)) {
                        field.set(obj, MaskingUtil.maskPhoneNumber(stringValue));
                    }
                } else {
                    // 중첩 객체 재귀 처리
                    processObject(value);
                }
                
            } catch (Exception e) {
                // 필드 접근 실패 시 무시
            }
        }
    }
    
    /**
     * 이름 필드인지 확인
     */
    boolean isNameField(String fieldName) {
        return fieldName.contains("name") || 
               fieldName.contains("nm") ||
               fieldName.equals("성명") ||
               fieldName.contains("username") ||
               fieldName.contains("membername");
    }
    
    /**
     * 이메일 필드인지 확인
     */
    boolean isEmailField(String fieldName) {
        return fieldName.contains("email") || 
               fieldName.contains("mail") ||
               fieldName.contains("이메일");
    }
    
    /**
     * 전화번호 필드인지 확인
     */
    boolean isPhoneField(String fieldName) {
        return fieldName.contains("phone") || 
               fieldName.contains("tel") ||
               fieldName.contains("mobile") ||
               fieldName.contains("전화") ||
               fieldName.contains("휴대폰") ||
               fieldName.contains("핸드폰");
    }
    
    /**
     * 기본 타입 또는 래퍼 클래스인지 확인
     */
    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || 
               clazz == Boolean.class || clazz == Character.class ||
               clazz == Byte.class || clazz == Short.class ||
               clazz == Integer.class || clazz == Long.class ||
               clazz == Float.class || clazz == Double.class;
    }
}
