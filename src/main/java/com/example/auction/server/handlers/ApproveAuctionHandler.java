package com.example.auction.server.handlers;

import com.example.auction.database.dao.AuctionDAO;
import com.example.auction.database.dao.NotificationDAO;
import com.example.auction.server.entity.Auction;
import com.example.auction.server.entity.AuctionStatus;
import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.server.service.AuctionService;
import com.example.auction.server.service.ClientService;
import com.example.auction.shared.dto.AuctionDetailsDTO;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.NotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApproveAuctionHandler extends RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(ApproveAuctionHandler.class);

  private final AuctionService auctionService = new AuctionService();
  @Override
  public void handle(ClientSession clientSession, MessageProtocol message, UserSession userSession) throws Exception {
    // KIỂM TRA QUYỀN (BẮT BUỘC): Tránh việc Member rành IT dùng công cụ gửi lậu gói tin lên Server
    UserSession currentSession = clientSession.getCurrentUserSession();
    if (currentSession == null || !"ADMIN".equals(currentSession.getRole().name())) {
      sendResponse(clientSession, new MessageProtocol(
        "APPROVE_AUCTION", null, "ERROR", "Quyền truy cập bị từ chối! Bạn không phải Admin."
      ));
      return;
    }

    // LẤY DỮ LIỆU TỪ GÓI TIN (Ví dụ: ID của phiên đấu giá cần duyệt)
    Long auctionId = (Long) message.data();

    try {
      // `THỰC THI XỬ LÝ XUỐNG DATABASE VIA DAO
      // Giả sử hàm này sẽ UPDATE trạng thái của phiên đấu giá từ 'PENDING' -> 'ACTIVE'
      log.info(auctionId+"ID CUA AUCTION DUOC UPDATE LA ");
      boolean isUpdated = AuctionDAO.getInstance().updateAuctionById(auctionId, AuctionStatus.ACTIVE.name());
      Auction auction = AuctionDAO.getInstance().getAuctionById(auctionId);
      auctionService.startAuctionTimer(auction);
      if (isUpdated) {
        // TRẢ VỀ PHẢN HỒI CHO ADMIN
        sendResponse(clientSession, new MessageProtocol(
          "APPROVE_AUCTION", null, "SUCCESS", "Đã duyệt phiên đấu giá thành công!"
        ));
        // Thông báo cho TẤT CẢ các Member online biết là có phòng đấu giá mới vừa mở để họ vào xem
        Auction auction1 = AuctionDAO.getInstance().getAuctionById(auctionId);
        AuctionDetailsDTO auctionDetailsDTO = new AuctionDetailsDTO(auction1);
        MessageProtocol broadcastMsg = new MessageProtocol("NEW_AUCTION_OPENED", auctionDetailsDTO, "INFO", "Phòng đấu giá mới đã mở!");
        ClientService.getInstance().broadcast(broadcastMsg);
        NotificationDTO notificationDTO = new NotificationDTO(null,auction.getSeller().getId(),"PHIÊN CỦA BẠN ĐÃ ĐƯỢC DUYỆT","Chúc mừng, phiên đấu giá của bạn đã được phê duyệt","APPROVE_AUCTION",null,auctionId,null);
        NotificationDAO.getInstance().addNotification(notificationDTO);
      }

      else {
        sendResponse(clientSession, new MessageProtocol(
          "APPROVE_AUCTION", null, "ERROR", "Không tìm thấy phiên đấu giá này hoặc lỗi cập nhật."
        ));
      }
    }
    catch (Exception e) {
      log.error("Exception", e);
      sendResponse(clientSession, new MessageProtocol(
        "APPROVE_AUCTION", null, "ERROR", "Lỗi Server: " + e.getMessage()
      ));
    }
  }


}
