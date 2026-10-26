package com.example.auction.shared.dto;

import java.io.Serializable;


public class AuctionPendingDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  public Long id;
  public String itemName;
  public String ownerName;
  public Double startPrice;
  public String status;

  public AuctionPendingDTO(Long id, String itemName, String ownerName, Double startPrice, String status) {
    this.id = id;
    this.itemName = itemName;
    this.ownerName = ownerName;
    this.startPrice = startPrice;
    this.status = status;
  }

  public static AuctionPendingDTO join(AuctionPendingDTO auctionDTO, ItemDTO itemDTO) {
    return new AuctionPendingDTO(
      auctionDTO.id,
      itemDTO.itemName(),
      auctionDTO.ownerName,
      auctionDTO.startPrice,
      auctionDTO.status
    );
  }

  public Long getId() { return id; }
  public String getItemName() { return itemName; }
  public String getOwnerName() { return ownerName; }
  public Double getStartPrice() { return startPrice; }
  public String getStatus() { return status; }
}

