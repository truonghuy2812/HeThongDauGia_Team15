package com.example.auction.shared.dto;


import com.example.auction.server.entity.Auction;

import java.io.Serializable;
import java.time.Instant;

public class AuctionDetailsDTO implements Serializable {
  public Long auctionId;
  public Double startPrice;
  public Double stepPrice;
  public Double currentHighestBid;
  public String currentHighestBidderUsername;
  private String startTime;
  private String endTime;
  public String status;
  public Integer bidCount;
  public Long itemId;
  public String itemName;
  public String tag;
  public String sellerUsername;
  public Long sellerId;
  public String itemImage;
  public String info;

  public AuctionDetailsDTO(Long auctionId, Double startPrice, Double stepPrice, Double currentHighestBid, String currentHighestBidderUsername, String startTime, String endTime, String status, Integer bidCount, Long itemId, String itemName, String tag, String sellerUsername, Long sellerId, String itemImage, String info) {
    this.auctionId = auctionId;
    this.startPrice = startPrice;
    this.stepPrice = stepPrice;
    this.currentHighestBid = currentHighestBid;
    this.currentHighestBidderUsername = currentHighestBidderUsername;
    this.startTime = startTime;
    this.endTime = endTime;
    this.status = status;
    this.bidCount = bidCount;
    this.itemId = itemId;
    this.itemName = itemName;
    this.tag = tag;
    this.sellerUsername = sellerUsername;
    this.sellerId = sellerId;
    this.itemImage = itemImage;
    this.info = info;

  }
  public AuctionDetailsDTO(Auction auction) {
    if (auction == null) return;

    this.auctionId = auction.getId();
    this.startPrice = auction.getStartPrice();
    this.stepPrice = auction.getStepPrice();
    this.currentHighestBid = auction.getCurrentHighestBid();

    if (auction.getCurrentHighestBidder() != null) {
      this.currentHighestBidderUsername = auction.getCurrentHighestBidder().getUsername();
    } else {
      this.currentHighestBidderUsername = "Chưa có";
    }

    // Chuyển đổi thời gian an toàn
    // Dùng String.valueOf() để chuyển từ LocalDateTime sang String an toàn
    this.startTime = (auction.getStartTime() != null) ? String.valueOf(auction.getStartTime()) : null;
    this.endTime = (auction.getEndTime() != null) ? String.valueOf(auction.getEndTime()) : null;
    this.status = (auction.getStatus() != null) ? auction.getStatus().toString() : null;
    this.bidCount = auction.getBidCount();

    if (auction.getItem() != null) {
      this.itemId = auction.getItem().getId();
      this.tag = auction.getItem().getTag();
      this.itemImage = auction.getItem().getImage();
      this.itemName = auction.getItem().getItemName();
    }

    if (auction.getSeller() != null) {
      this.sellerUsername = auction.getSeller().getUsername();
      this.sellerId = auction.getSeller().getId();
    }
  }
  public void setCurrentHighestBid(Double currentHighestBid) {
    this.currentHighestBid = currentHighestBid;
  }

  public static AuctionDetailsDTO join(AuctionDTO auctionDTO, ItemDTO itemDTO) {
    return new AuctionDetailsDTO(
            auctionDTO.id(),
            auctionDTO.startPrice(),
            auctionDTO.stepPrice(),
            auctionDTO.currentHighestBid(),
            auctionDTO.currentHighestBidderUsername(),
            String.valueOf(auctionDTO.startTime()), // Ép kiểu sang String đơn giản
            String.valueOf(auctionDTO.endTime()),   // Ép kiểu sang String đơn giản
            String.valueOf(auctionDTO.status()),
            auctionDTO.bidCount(),
            itemDTO.id(),
            itemDTO.itemName(),
            itemDTO.tag(),
            auctionDTO.sellerUsername(),
            itemDTO.sellerId(),
            itemDTO.imageUrl(),
            itemDTO.info()
    );
  }
  private static final long serialVersionUID = 1L;

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }
}
