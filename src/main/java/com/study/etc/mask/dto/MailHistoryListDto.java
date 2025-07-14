package com.study.etc.mask.dto;

import java.util.List;

/**
 * 메일 이력 목록 DTO
 */
public class MailHistoryListDto {
    private int totalCount;
    private List<MailHistoryDto> mailHistory;
    
    public MailHistoryListDto(int totalCount, List<MailHistoryDto> mailHistory) {
        this.totalCount = totalCount;
        this.mailHistory = mailHistory;
    }
    
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    
    public List<MailHistoryDto> getMailHistory() { return mailHistory; }
    public void setMailHistory(List<MailHistoryDto> mailHistory) { this.mailHistory = mailHistory; }
}
