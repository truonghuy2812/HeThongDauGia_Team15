package com.example.auction.server.service;

import com.example.auction.server.entity.Auction;
import com.example.auction.shared.dto.AuctionDetailsDTO;
import com.example.auction.shared.dto.MessageProtocol;

import java.time.Instant;

public interface AuctionDependencies {

  // ---- AuctionDAO-like ----
  Long addAuctionAndGetId(AuctionDetailsDTO dto);
  Auction getAuctionById(Long auctionId);
  void closeAuctionById(long auctionId, long winnerId);
  boolean updateAuctionStatus(long auctionId, String newStatus);
  void updateAuctionEndTime(long auctionId, Instant newEndTime);

  // ---- PaymentAccountDAO-like ----
  void updateAccountByOwnerId(long ownerId, double balance, double frozenBalance);

  // ---- Broadcast ----
  void broadcast(MessageProtocol message);
}