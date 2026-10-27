package com.example.auction.server.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Auction implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        UPCOMING, ACTIVE, ENDED, CANCELLED
    }

    private Long id;
    private Double startPrice;
    private Double stepPrice;
    private Double currentHighestBid;
    private User currentHighestBidder;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Status status;
    private Integer bidCount;
    private Item item;
    private User seller;

    public Auction() {}

    // Getters
    public Long getId() { return id; }
    public Double getStartPrice() { return startPrice; }
    public Double getStepPrice() { return stepPrice; }
    public Double getCurrentHighestBid() { return currentHighestBid; }
    public User getCurrentHighestBidder() { return currentHighestBidder; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public Status getStatus() { return status; }
    public Integer getBidCount() { return bidCount; }
    public Item getItem() { return item; }
    public User getSeller() { return seller; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setStartPrice(Double startPrice) { this.startPrice = startPrice; }
    public void setStepPrice(Double stepPrice) { this.stepPrice = stepPrice; }
    public void setCurrentHighestBid(Double currentHighestBid) { this.currentHighestBid = currentHighestBid; }
    public void setCurrentHighestBidder(User currentHighestBidder) { this.currentHighestBidder = currentHighestBidder; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setStatus(Status status) { this.status = status; }
    public void setBidCount(Integer bidCount) { this.bidCount = bidCount; }
    public void setItem(Item item) { this.item = item; }
    public void setSeller(User seller) { this.seller = seller; }
}