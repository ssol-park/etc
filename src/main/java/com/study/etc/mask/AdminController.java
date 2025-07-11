package com.study.etc.mask;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 어드민 페이지 컨트롤러 (테스트용)
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    /**
     * 계정 관리 페이지 - 마스킹 적용
     */
    @PersonalDataMasking
    @GetMapping("/accounts")
    public List<UserDto> getAccounts() {
        return Arrays.asList(
            new UserDto("홍길동", "hong123@gmail.com", "01012345678"),
            new UserDto("김철수", "kimcs456@naver.com", "01098765432"),
            new UserDto("이영희", "lee789@daum.net", "0212344567")
        );
    }
    
    /**
     * 메일 발송 이력 관리 페이지 - 마스킹 적용
     */
    @PersonalDataMasking
    @GetMapping("/mail-history")
    public List<MailHistoryDto> getMailHistory() {
        return Arrays.asList(
            new MailHistoryDto("홍길동", "hong123@gmail.com", "회원가입 인증", "2024-01-15 10:30:00"),
            new MailHistoryDto("김철수", "kimcs456@naver.com", "비밀번호 재설정", "2024-01-15 11:15:00")
        );
    }
    
    /**
     * 실제 메일 발송 기능 - 마스킹 적용 안함
     */
    @GetMapping("/send-mail")
    public String sendMail() {
        // 실제 메일 발송 로직에서는 마스킹되지 않은 원본 이메일을 사용
        UserDto user = new UserDto("홍길동", "hong123@gmail.com", "01012345678");
        
        // 이메일 발송 로직 (가상)
        return "메일 발송 완료: " + user.getEmail();
    }
    
    /**
     * 사용자 DTO
     */
    public static class UserDto {
        private String name;
        private String email;
        private String phoneNumber;
        
        public UserDto(String name, String email, String phoneNumber) {
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
        }
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }
    
    /**
     * 메일 이력 DTO
     */
    public static class MailHistoryDto {
        private String receiverName;
        private String receiverEmail;
        private String subject;
        private String sentDate;
        
        public MailHistoryDto(String receiverName, String receiverEmail, String subject, String sentDate) {
            this.receiverName = receiverName;
            this.receiverEmail = receiverEmail;
            this.subject = subject;
            this.sentDate = sentDate;
        }
        
        // Getters and Setters
        public String getReceiverName() { return receiverName; }
        public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
        
        public String getReceiverEmail() { return receiverEmail; }
        public void setReceiverEmail(String receiverEmail) { this.receiverEmail = receiverEmail; }
        
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public String getSentDate() { return sentDate; }
        public void setSentDate(String sentDate) { this.sentDate = sentDate; }
    }
}
