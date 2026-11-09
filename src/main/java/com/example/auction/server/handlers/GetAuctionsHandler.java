package com.example.auction.server.handlers;

import com.example.auction.database.dao.AuctionDAO;
import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.shared.dto.AuctionDetailsDTO;
import com.example.auction.shared.dto.MessageProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * GetAuctionsHandler - Lấy danh sách sản phẩm
 * - Mỗi product = 1 AuctionDTO
 * - GUEST cũng xem được
 */

public class GetAuctionsHandler extends RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(GetAuctionsHandler.class);

  @Override
  public void handle(ClientSession session, MessageProtocol message, UserSession userSession ) {
    try {
//      // lấy dữ liệu thật từ DB
//      List<AuctionDetailsDTO> auctions = AuctionDAO.getInstance().getAllActiveAuctions();
      String keyword = message.data() instanceof String s ? s.trim() : "";
      List<AuctionDetailsDTO> auctions = keyword.isEmpty()
        ? AuctionDAO.getInstance().getAllActiveAuctions()
        : AuctionDAO.getInstance().searchActiveAuctions(keyword);

      // gửi phản hồi về cho Client
      sendResponse(session, new MessageProtocol(
        "GET_AUCTIONS_LIST",
          auctions,
        "SUCCESS",
        keyword.isEmpty() ? null : "Kết quả tìm kiếm: " + auctions.size() + " phiên" // truowcs là null
      ));
    }
    catch (Exception e) {
      sendResponse(session, new MessageProtocol(
        "GET_AUCTIONS_LIST",
        null,
        "ERROR",
        "Lỗi lấy sản phẩm: " + e.getMessage()
      ));
      log.error("Exception", e);
    }
  }
}
