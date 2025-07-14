package com.study.etc.mask.dto;

/**
 * 공통 응답 DTO
 */
public class ResponseDto<T> {
    private T data;
    
    public ResponseDto(T data) {
        this.data = data;
    }
    
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
