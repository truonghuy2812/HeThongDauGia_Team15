package com.example.auction.server.handlers;

import com.example.auction.database.dao.AuctionDAO;
import com.example.auction.database.dao.NotificationDAO;
import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.server.service.ClientService;
import com.example.auction.shared.dto.AuctionDetailsDTO;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.NotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class AddAuctionHandler extends RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(AddAuctionHandler.class);

//    @Override
    public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws Exception {
        log.info("DA THEM PHIEN O DAY");
        AuctionDetailsDTO auctionDetailsDTO = (AuctionDetailsDTO) message.data();
        Long idAuction = AuctionDAO.getInstance().addAuctionAndGetID(auctionDetailsDTO);
        if (idAuction == null){
            log.info("Thêm phiên thất bại!");
            MessageProtocol messageProtocol = new MessageProtocol("ADD_AUCTION",auctionDetailsDTO,"FAILED","Vui lòng kiểm tra lại thông tin phiên!");
            sendResponse(session,messageProtocol);
            return;
        }
        log.info("Thêm phiên thành công!");
        MessageProtocol messageProtocol = new MessageProtocol("ADD_AUCTION",auctionDetailsDTO,"SUCCESS","TUYET VOI DA THEM THANH CONG ROI AE OI");
        sendResponse(session,messageProtocol);
        ClientService.getInstance().broadcast(messageProtocol); // day chi la broadcast den cac client online
        //  THÊM ĐOẠN BROADCAST NÀY CHO ADMIN ---
        // Đóng gói thông tin phiên vừa tạo gửi đi toàn mạng
        AuctionDetailsDTO sendAuction = new AuctionDetailsDTO(idAuction.longValue(), auctionDetailsDTO.startPrice,
                auctionDetailsDTO.stepPrice, auctionDetailsDTO.currentHighestBid, auctionDetailsDTO.currentHighestBidderUsername, auctionDetailsDTO.startTime,auctionDetailsDTO.endTime,
                auctionDetailsDTO.status,auctionDetailsDTO.bidCount,auctionDetailsDTO.itemId,auctionDetailsDTO.itemName,auctionDetailsDTO.tag,auctionDetailsDTO.sellerUsername,auctionDetailsDTO.sellerId,auctionDetailsDTO.itemImage,auctionDetailsDTO.info);
        MessageProtocol notifyAdminMsg = new MessageProtocol("SERVER_NEW_PENDING_AUCTION", sendAuction, "INFO", null);
        log.info("DA GUI THONG BAO CHO ADMIN");
        NotificationDTO notificationDTO = new NotificationDTO(null,userSession.getId(),"BAN DA GUI PHIEN DUYET THANH CONG","Bạn đã tạo phiên mới thành công, đang đợi admin duyệt","NEW_AUCTION",null,idAuction, Instant.now());
        NotificationDAO.getInstance().addNotification(notificationDTO);
        ClientService.getInstance().sendToAllAdmins(notifyAdminMsg);
    }
}
