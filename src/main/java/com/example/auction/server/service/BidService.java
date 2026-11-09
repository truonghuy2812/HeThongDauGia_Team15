package com.example.auction.server.service;

/**
 * BidService - Business logic cho bids
 */

import com.example.auction.shared.dto.BidDTO;

public class BidService {
  public boolean validateBid(BidDTO bidDTO, Double currentBid, String sellerUsername) {
    // Validation logic
    if (bidDTO.amount() <= currentBid) {
      return false; // Amount quá thấp
    }

    return true;
  }

}
