package com.example.auction.shared.dto;

import java.io.Serializable;

public class AutobidDTO implements Serializable {
  public long auctionId;
  public long userId;
  public double maxPrice;
  public int isOn;

  public AutobidDTO(long auctionId, long userId, double maxPrice, int isOn) {
    this.auctionId = auctionId;
    this.userId = userId;
    this.maxPrice = maxPrice;
    this.isOn = isOn;
  }
}
