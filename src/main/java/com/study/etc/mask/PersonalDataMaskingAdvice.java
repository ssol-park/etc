package com.study.etc.mask;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 개인정보 마스킹 처리를 위한 ResponseBodyAdvice
 * ResponseDto의 data 필드에서 개인정보 필드만 마스킹 처리
 */
@ControllerAdvice
public class PersonalDataMaskingAdvice implements ResponseBodyAdvice<Object> {
    
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
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
     * ResponseDto에서 data 필드를 찾아 개인정보 마스킹
     */
    Object maskPersonalData(Object responseDto) {
        if (responseDto == null) {
            return null;
        }
        
        try {
            // ResponseDto에서 data 필드 추출
            Field dataField = responseDto.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            Object data = dataField.get(responseDto);
            
            if (data == null) {
                return responseDto;
            }
            
            // data가 List면 각 요소 마스킹, 아니면 직접 마스킹
            if (data instanceof List) {
                List<?> dataList = (List<?>) data;
                for (Object item : dataList) {
                    maskObjectFields(item);
                }
            } else {
                maskObjectFields(data);
            }
            
        } catch (Exception e) {
            // data 필드 없거나 접근 실패 시 원본 반환
        }
        
        return responseDto;
    }
    
    /**
     * 객체의 개인정보 필드들을 마스킹
     */
    private void maskObjectFields(Object obj) {
        if (obj == null) {
            return;
        }
        
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);
                
                if (value instanceof String) {
                    String stringValue = (String) value;
                    String fieldName = field.getName().toLowerCase();
                    
                    // 필드명 기반 마스킹
                    if (isNameField(fieldName)) {
                        field.set(obj, MaskingUtil.maskName(stringValue));
                    } else if (isEmailField(fieldName)) {
                        field.set(obj, MaskingUtil.maskEmail(stringValue));
                    } else if (isPhoneField(fieldName)) {
                        field.set(obj, MaskingUtil.maskPhoneNumber(stringValue));
                    }
                }
            }
        } catch (Exception e) {
            // 필드 접근 실패 시 무시
        }
    }
    
    /**
     * 이름 필드인지 확인
     */
    private boolean isNameField(String fieldName) {
        return fieldName.contains("name");
    }
    
    /**
     * 이메일 필드인지 확인
     */
    private boolean isEmailField(String fieldName) {
        return fieldName.contains("mail");
    }
    
    /**
     * 전화번호 필드인지 확인
     */
    private boolean isPhoneField(String fieldName) {
        return fieldName.contains("phone") || fieldName.contains("tel");
    }
}
