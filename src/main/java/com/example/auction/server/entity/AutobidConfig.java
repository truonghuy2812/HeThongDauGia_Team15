package com.example.auction.server.entity;

import com.example.auction.shared.dto.AutobidDTO;

import java.time.Instant;

public class AutobidConfig extends Entity{
    private long auctionId;
    private long userId;
    private int isOn;
    private double maxPrice;

    public AutobidConfig(Long id, Instant createdAt, Instant updatedAt, long auctionId, long userId, int isOn, double maxPrice) {
        super(id, createdAt, updatedAt);
        this.auctionId = auctionId;
        this.userId = userId;
        this.isOn = isOn;
        this.maxPrice = maxPrice;
    }
    public AutobidConfig() {
    }
    public AutobidDTO convert(){
        return new AutobidDTO(
                auctionId,
                userId,
                maxPrice,
                isOn
        );
    }

    public long getAuctionId() {
        return auctionId;
    }

    public long getUserId() {
        return userId;
    }

    public int getIsOn() {
        return isOn;
    }

    public double getMaxPrice() {
        return maxPrice;
    }
}
