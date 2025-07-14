package com.study.etc.mask.dto;

import java.util.List;

/**
 * 사용자 목록 DTO
 */
public class UserListDto {
    private int totalCount;
    private List<UserDto> users;
    private String message;
    
    public UserListDto(int totalCount, List<UserDto> users, String message) {
        this.totalCount = totalCount;
        this.users = users;
        this.message = message;
    }
    
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    
    public List<UserDto> getUsers() { return users; }
    public void setUsers(List<UserDto> users) { this.users = users; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
