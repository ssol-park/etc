package com.study.etc.mask.dto;

/**
 * 메일 이력 DTO
 */
public class MailHistoryDto {
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
    
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    
    public String getReceiverEmail() { return receiverEmail; }
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail = receiverEmail; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getSentDate() { return sentDate; }
    public void setSentDate(String sentDate) { this.sentDate = sentDate; }
}
