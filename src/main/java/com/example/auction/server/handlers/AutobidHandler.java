package com.example.auction.server.handlers;

import com.example.auction.database.dao.AuctionDAO;
import com.example.auction.database.dao.AutobidConfigDAO;
import com.example.auction.database.dao.UserDAO;
import com.example.auction.server.entity.Auction;
import com.example.auction.server.entity.AutobidConfig;
import com.example.auction.server.entity.User;
import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.server.service.ClientService;
import com.example.auction.shared.dto.AutobidDTO;
import com.example.auction.shared.dto.BidDTO;
import com.example.auction.shared.dto.MessageProtocol;

import java.sql.SQLException;
import java.time.Instant;

public class AutobidHandler extends RequestHandler{
  private final BidHandler bidHandler = new BidHandler();
  @Override
  public void handle(ClientSession session, MessageProtocol message, UserSession userSession){
    AutobidDTO data = (AutobidDTO) message.data();
    if (data.isOn == 0){
      // chỉ là gửi request để tắt.
      try {
        MessageProtocol messageProtocol = new MessageProtocol(
            "AUTOBID",
            data,
            "SUCCESS",
            "Bạn đã tắt autobid thành công"
        );
        AutobidConfigDAO.getInstance().updateConfig(data.auctionId, data.userId, data.isOn, data.maxPrice);
        ClientService.getInstance().sendTo(data.userId, messageProtocol);
      } catch (SQLException e) {
        MessageProtocol response = new MessageProtocol(
            "AUTOBID" ,
            null,
            "FAILED",
            "Cập nhật cấu hình autobid thất bại!"
        );
        ClientService.getInstance().sendTo(data.userId, response);
      }
      return;
    }
    try{
      Auction auction = AuctionDAO.getInstance().getAuctionById(data.auctionId);
      User newBidder = UserDAO.getInstance().getUserbyId(data.userId);
      // kiểm tra lệnh đặt tối thiểu
      if (data.maxPrice < auction.getCurrentHighestBid() + auction.getStepPrice()){
        MessageProtocol response = new MessageProtocol(
            "AUTOBID" ,
            null,
            "FAILED",
            "Cập nhật cấu hình autobid thất bại do giá trần bạn đặt không đủ qua!"
        );
        ClientService.getInstance().sendTo(data.userId, response);
        return;
      }
      if (data.maxPrice > newBidder.getPaymentAccount().getBalance()){
        MessageProtocol response = new MessageProtocol(
          "AUTOBID" ,
          null,
          "FAILED",
          "Bạn không đủ tiền"
        );
        ClientService.getInstance().sendTo(data.userId, response);
        return;
      }


      AutobidConfig oldHighestConfig = AutobidConfigDAO.getInstance().getFirstConfigByMaxPrice(data.auctionId);
      boolean oldHighestConfigIsExisted = oldHighestConfig.getUserId() != 0;
      boolean oldHighestConfigIsLeader = oldHighestConfig.getUserId() == auction.getCurrentHighestBidder().getId();
      if (oldHighestConfigIsExisted && oldHighestConfigIsLeader){
        //TH1: autobid vs autobid
        handleAutobidVsAutobid(data, session, auction, newBidder);
      }else{
        //kiểm tra để tránh trường hợp client đặt xong lại cài thêm autobid để chắc chắn.
        if (data.userId != auction.getCurrentHighestBidder().getId()){
          //TH2: đặt autobid lúc đã có người đặt tay trước vì đã kiểm tra qua bước giá nên lấy luôn, không cần kiểm tra lại
          handleManualBidVsAutoBid(session, auction, newBidder);
        }
      }
      // xong mới lưu vào database để chỉ cần lấy thằng lớn nhất ra.
      AutobidConfigDAO.getInstance().updateConfig(data.auctionId, data.userId, data.isOn, data.maxPrice);
      AutobidDTO config = new AutobidDTO(data.auctionId, data.userId, data.maxPrice, data.isOn);
      MessageProtocol response = new MessageProtocol(
          "AUTOBID" ,
          config,
          "SUCCESS",
          "Đã cập nhật cấu hình autobid thành công!"
      );
      ClientService.getInstance().sendTo(data.userId, response);
    }catch (SQLException e){
      MessageProtocol response = new MessageProtocol(
          "AUTOBID" ,
          null,
          "FAILED",
          "Cập nhật cấu hình autobid thất bại!"
      );
      ClientService.getInstance().sendTo(data.userId, response);
    }
  }
  private void handleManualBidVsAutoBid(ClientSession newSession, Auction auction, User newBidder){
    double requiredPrice = auction.getCurrentHighestBid() + auction.getStepPrice();
    // lấy người mới
    handleAutobid(newSession, auction, newBidder, requiredPrice);
  }
  private void handleAutobid(ClientSession session, Auction auction, User newBidder, double price){
    BidDTO bid = new BidDTO(
        auction.getId(),
        newBidder.getId(),
        newBidder.getUsername(),
        price,
        Instant.now(),
        "SUCCESS"
    );
    Object object = BidHandler.auctionLocks.computeIfAbsent(auction.getId(), _ -> new Object());
    synchronized (object){
      BidHandler.executor.submit(() -> bidHandler.processBid(session, bid, "AUTOBID",newBidder));
    }
  }
  private void maxPriceNoti(long userId){
    MessageProtocol response = new MessageProtocol(
        "AUTOBID" ,
        null,
        "NOTI",
        "Bạn đã đạt mức trần!"
    );
    ClientService.getInstance().sendTo(userId, response);
  }
  private void handleAutobidVsAutobid(AutobidDTO newConfig, ClientSession session, Auction auction, User newBidder) throws SQLException{
    AutobidConfig oldHighestConfig = AutobidConfigDAO.getInstance().getFirstConfigByMaxPrice(newConfig.auctionId);
    AutobidDTO oldConfig = oldHighestConfig.convert();
    User oldBidder = UserDAO.getInstance().getUserbyId(oldConfig.userId);
    double requiredPrice;
    ClientSession oldSession = ClientService.getInstance().getClientSessionById(oldConfig.userId);
    if (oldConfig.maxPrice >= newConfig.maxPrice){
      // lấy old vì đã được tạo trước.
      requiredPrice = newConfig.maxPrice + auction.getStepPrice();
      if (requiredPrice >= oldConfig.maxPrice){
        requiredPrice = oldConfig.maxPrice;
        // thông báo đạt mức trần
        maxPriceNoti(oldConfig.userId);
      }
      maxPriceNoti(newConfig.userId);
      // chỗ này break bước giá
      handleAutobid(session, auction, newBidder, newConfig.maxPrice);
      Auction newAuction = AuctionDAO.getInstance().getAuctionById(auction.getId());
      handleAutobid(oldSession, newAuction, oldBidder, requiredPrice);
    } else {
      // lấy new
      requiredPrice = oldConfig.maxPrice + auction.getStepPrice();
      if (requiredPrice >= newConfig.maxPrice){
        requiredPrice = newConfig.maxPrice;
        // thông báo đạt mức trần
        maxPriceNoti(newConfig.userId);
      }
      maxPriceNoti(oldConfig.userId);
      //chỗ này cũng break bước giá để lưu lịch sử.
      handleAutobid(oldSession, auction, oldBidder, oldConfig.maxPrice);
      Auction newAuction = AuctionDAO.getInstance().getAuctionById(auction.getId());
      handleAutobid(session, newAuction, newBidder, requiredPrice);
    }
  }
}
