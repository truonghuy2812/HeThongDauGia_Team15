package com.example.auction.server.handlers;

import com.example.auction.database.dao.AuctionDAO;
import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.shared.dto.AuctionPendingDTO;
import com.example.auction.shared.dto.MessageProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GetPendingAuctionsHandler extends RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(GetPendingAuctionsHandler.class);

  @Override
  public void handle(ClientSession clientSession, MessageProtocol message, UserSession userSession) {
    try {
      // lay du lieu that tuw db
      List<AuctionPendingDTO> auctions = AuctionDAO.getInstance().getAllPendingAuctions();

      // gui phan hoi cho client
      sendResponse(clientSession, new MessageProtocol(
        "GET_PENDING_AUCTIONS",
        auctions,
        "SUCCESS",
        null
      ));
    }
    catch (Exception e) {
      sendResponse(clientSession, new MessageProtocol(
        "GET_PENDING_AUCTIONS",
        null,
        "ERROR",
        "Loi lay sp " +e.getMessage()
      ));
      log.error("Exception", e);
    }
  }
}
