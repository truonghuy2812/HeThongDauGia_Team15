package com.example.auction.shared.dto;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

public class NotificationDTO implements Serializable {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String type;
    private Boolean isRead;
    private Long relatedAuctionId;
    private Instant createdAt;
    // Constructor không tham số
    public NotificationDTO() {
    }
    public NotificationDTO(Long id, Long userId, String title, String message,
                           String type, Boolean isRead, Long relatedAuctionId, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.relatedAuctionId = relatedAuctionId;
        this.createdAt = createdAt;
    }

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public Long getRelatedAuctionId() { return relatedAuctionId; }
    public void setRelatedAuctionId(Long relatedAuctionId) { this.relatedAuctionId = relatedAuctionId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
