package com.example.auction.server.service;

import com.example.auction.database.dao.AuctionDAO;
import com.example.auction.database.dao.PaymentAccountDAO;
import com.example.auction.server.entity.Auction;
import com.example.auction.shared.dto.AuctionDetailsDTO;
import com.example.auction.shared.dto.MessageProtocol;

import java.time.Instant;

public class DefaultAuctionDependencies implements AuctionDependencies {

  @Override
  public Long addAuctionAndGetId(AuctionDetailsDTO dto) {
    return AuctionDAO.getInstance().addAuctionAndGetID(dto);
  }

  @Override
  public Auction getAuctionById(Long auctionId) {
    return AuctionDAO.getInstance().getAuctionById(auctionId);
  }

  @Override
  public void closeAuctionById(long auctionId, long winnerId) {
    AuctionDAO.getInstance().closeAuctionbyId(auctionId, winnerId);
  }

  @Override
  public boolean updateAuctionStatus(long auctionId, String newStatus) {
    return AuctionDAO.getInstance().updateAuctionById(auctionId, newStatus);
  }

  @Override
  public void updateAuctionEndTime(long auctionId, Instant newEndTime) {
    AuctionDAO.getInstance().updateEndTimeById(auctionId, newEndTime);
  }

  @Override
  public void updateAccountByOwnerId(long ownerId, double balance, double frozenBalance) {
    PaymentAccountDAO.getInstance().updateAccountByOwnerId(ownerId, balance, "SUCCESS");
  }

  @Override
  public void broadcast(MessageProtocol message) {
    ClientService.getInstance().broadcast(message);
  }
}