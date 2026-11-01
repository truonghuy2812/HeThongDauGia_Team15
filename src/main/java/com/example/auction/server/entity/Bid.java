package com.example.auction.server.entity;

import java.time.Instant;

public class Bid {
    private User bidder;
    private double amount;
    private Instant bidTime;

    public Bid(User bidder, double amount) {
        this.bidder = bidder;
        this.amount = amount;
        this.bidTime = Instant.now();
    }

    public User getBidder() {
        return bidder;
    }

    public double getAmount() {
        return amount;
    }

    public Instant getBidTime() {
        return bidTime;
    }
}