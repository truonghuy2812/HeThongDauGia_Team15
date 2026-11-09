package com.example.auction.server.handlers;

import com.example.auction.database.dao.*;
import com.example.auction.server.entity.Auction;
import com.example.auction.server.entity.AutobidConfig;
import com.example.auction.server.entity.Role;
import com.example.auction.server.entity.User;
import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.server.service.AuctionService;
import com.example.auction.server.service.ClientService;
import com.example.auction.shared.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BidHandler extends RequestHandler {
  private static final AuctionService auctionService = new AuctionService();
  static final ExecutorService executor = Executors.newFixedThreadPool(15);
  static final ConcurrentHashMap<Long , Object> auctionLocks = new ConcurrentHashMap<>();
  private static final Logger log = LoggerFactory.getLogger(BidHandler.class);
  @Override
  public void handle(ClientSession session, MessageProtocol message, UserSession userSession) {
    BidDTO bidDTO = (BidDTO) message.data();
    String type = bidDTO.status();
    Runnable bidProcess = () -> {
      try{
        User user = UserDAO.getInstance().getUserbyId(bidDTO.bidderId());
        validateRole(userSession, user);
        //PaymentAccountDTO account = PaymentAccountDAO.getInstance().getAccountByOwnerId(bidDTO.bidderId());
        Object lock = auctionLocks.computeIfAbsent(bidDTO.auctionId(), _ -> new Object());
        synchronized (lock){
          processBid(session , bidDTO, type, user);
        }
      }catch(IllegalArgumentException e){
        sendResponse(session, new MessageProtocol(
            "NEW_BID",
            null,
            "ERROR",
            "Lỗi xử lý bid: " + e.getMessage()
        ));
        e.printStackTrace();
      }catch (Exception e){
        sendResponse(session, new MessageProtocol(
            "NEW_BID",
            null,
            "ERROR",
            "Lỗi xử lý bid: Hệ thống gặp lỗi nghiêm trọng"
        ));
        e.printStackTrace();
      }
    };
    executor.submit(bidProcess);
  }
  private void validateRole(UserSession userSession, User user) throws IllegalArgumentException{
    if (userSession == null) {
      throw new IllegalArgumentException("Phải login trước khi đấu giá");
    }

    if (userSession.getRole() != Role.MEMBER) {
      throw new IllegalArgumentException("Chỉ BIDDER hoặc SELLER mới được đấu giá");
    }
    if (!user.getUsername().equals(userSession.getUserName())) {
      throw new IllegalArgumentException("Username không trùng");
    }
  }
   void processBid(ClientSession session, BidDTO bidDTO, String type, User bidder){
    try {
      Auction auction = AuctionDAO.getInstance().getAuctionById(bidDTO.auctionId());
      System.out.println("ID TRONG BIDHANDLER: " + auction.getId());
      System.out.println(auction.getStatus().name());
      if (!auction.getStatus().name().equals("ACTIVE")){
        System.out.println(auction.getStatus().name());
        throw new IllegalArgumentException("Không chấp nhận vì phiên đã kết thúc hoặc chưa bắt đầu!");
      }
      if (bidDTO.amount() == null || bidDTO.amount() <= 0) {
        throw  new IllegalArgumentException("Số tiền phải > 0");
      }

      if (auction.getCurrentHighestBid() + auction.getStepPrice() > bidDTO.amount() && !"AUTOBID".equals(type)) {
        throw new IllegalArgumentException("Giá đặt chưa đủ!");
      }

      if(bidDTO.amount() > bidder.getPaymentAccount().getBalance()){
        throw new IllegalArgumentException("Số dư không đủ để đặt giá!");
      }

      AutobidConfig oldConfig = AutobidConfigDAO.getInstance().getFirstConfigByMaxPrice(bidDTO.auctionId());
      boolean oldConfigIsExisted = oldConfig.getUserId() != 0;
      boolean oldConfigIsLeader = oldConfig.getUserId() == auction.getCurrentHighestBidder().getId();
      System.out.println(oldConfigIsExisted + " " + oldConfigIsLeader);
      if (oldConfigIsExisted && oldConfigIsLeader && !"AUTOBID".equals(type)){
        handleAutobidVsManualBid(session, bidDTO, auction, oldConfig, type);
      }
      else {
        updateAuctionAndAddBidHistory(session, auction, bidDTO);
      }
    }
    catch (Exception e) {
      sendResponse(session, new MessageProtocol(
          "NEW_BID",
          null,
          "ERROR",
          "Lỗi xử lý bid: " + e.getMessage()
      ));
      e.printStackTrace();
    }
  }
  private void broadcast(BidDTO bidDTO){
    // BROADCAST: Tất cả clients nhận update

    MessageProtocol broadcastMsg = new MessageProtocol(
        "NEW_BID",  // type: Broadcast new bid
        bidDTO,   // data: BidDTO
        "SUCCESS",   // data: BidDTO
        null   // data: BidDTO
    );
    // broadcast tơ tất cả connected clients
    // ClientService sẽ gửi message tới tất cả ClientSession
    ClientService.getInstance().broadcast(broadcastMsg);
    PaymentDTO paymentDTO = new PaymentDTO(bidDTO.bidderId(),bidDTO.amount(),"BID",Instant.now(),null,"Bạn đã đấu giá thành công");
    PaymentRequestDAO.getInstance().createPaymentRequest(paymentDTO);

  }
  private void outbidHandle(long auctionId){
    Auction auction = AuctionDAO.getInstance().getAuctionById(auctionId);
    User outBidUser = auction.getCurrentHighestBidder();
    if (outBidUser != null){
      // refund tienf cho ng bij outbid
      PaymentAccountDTO outBidUserAccount = PaymentAccountDAO.getInstance().getAccountByOwnerId(outBidUser.getId());
      double unfreezeAmount = auction.getCurrentHighestBid();
      PaymentAccountDAO.getInstance().updateAccountByOwnerId(
          outBidUser.getId(),
          outBidUserAccount.balance() + unfreezeAmount, // cong lai vao balance
          null
      );
      BidDTO outBidDTO = new BidDTO(auction.getId(),
          outBidUser.getId(),
          outBidUser.getUsername(),
          auction.getCurrentHighestBid(),
          auction.getUpdatedAt(),
          "OUT_BID"
      );
      PaymentDTO paymentDTO = new PaymentDTO(outBidUser.getId(),auction.getCurrentHighestBid(),"OUT_BID",Instant.now(),null,"Bạn bị out_bid tiền đã được hoàn");
      PaymentRequestDAO.getInstance().createPaymentRequest(paymentDTO);
      MessageProtocol messageProtocol = new MessageProtocol("OUT_BID",outBidDTO,null,"DA CO NGUOI DAT GIA CAO HON BAN, BAN DA DUOC HOAN TIEN");
      ClientService.getInstance().sendTo(outBidUser.getId(),messageProtocol);
      NotificationDTO notificationDTO = new NotificationDTO(null, outBidUser.getId(),"Bạn đã bị OUTBID","Auction bạn đặt đã bị Outbid tại phiên bán: " + auction.getItem().getItemName(),"OUT_BID",null,auction.getId(), Instant.now());
      NotificationDAO.getInstance().addNotification(notificationDTO);
    }
  }
  synchronized void updateAuctionAndAddBidHistory(ClientSession session, Auction auction, BidDTO bidDTO){
      outbidHandle(auction.getId());
      AuctionDAO.getInstance().updateHighestBid(auction.getId(), bidDTO.amount(), bidDTO.bidderId());
      long timeLimit = Duration.between(Instant.now(),auction.getEndTime()).toSeconds();
      if (timeLimit <= 60){
        auctionService.antiSniping(auction);
        AuctionDetailsDTO auctionDetailsDTO = new AuctionDetailsDTO(auction);
        ClientService.getInstance().broadcast(new MessageProtocol("ANTI_SNIPE",auctionDetailsDTO,"SUCCESS","ANTI SNIPE"));
      }
      BidDAO.getInstance().addBid(bidDTO); // lưu vào lịch sử bid
      // cập nhật giá mới
      log.info("Bid accepted: " + auction.getId() +
        " bid " + bidDTO.amount() +
        " for auction " + bidDTO.auctionId());
      double amount = bidDTO.amount();
      // tru tien nguoi dung sau khi bid dat thanh cong
      PaymentAccountDTO before = PaymentAccountDAO.getInstance().getAccountByOwnerId(bidDTO.bidderId());
      PaymentAccountDAO.getInstance().updateAccountByOwnerId(
          before.ownerId(),
          before.balance() - amount,
          null); // balance giamr, frozen tawng
      PaymentAccountDTO paymentAccount = PaymentAccountDAO.getInstance().getAccountByOwnerId(bidDTO.bidderId());
      MessageProtocol withdrawMessageProtocol = new MessageProtocol(
          "WITHDRAW",
          paymentAccount,
          "SUCCESS",
          "THANH TOÁN THÀNH CÔNG!"
      );
    broadcast(bidDTO);
    if (session != null){
        //RESPONSE: Send withdraw to bidder
        sendResponse(session, withdrawMessageProtocol);
        // RESPONSE: Send success to bidder
      }
  }
  private void maxPriceNoti(long userId){
    MessageProtocol response = new MessageProtocol(
        "AUTOBID" ,
        null,
        "FAILED",
        "Bạn đã đạt mức trần!"
    );
    ClientService.getInstance().sendTo(userId, response);
  }
  private void handleAutobidVsManualBid(ClientSession newSession, BidDTO bidDTO, Auction auction, AutobidConfig oldAutobidConfig, String type) throws SQLException {
    AutobidDTO oldConfig = oldAutobidConfig.convert();
    User oldBidder = UserDAO.getInstance().getUserbyId(oldConfig.userId);
    ClientSession oldSession = ClientService.getInstance().getClientSessionById(oldConfig.userId);
    BidDTO newBidForOldBidder = new BidDTO(
        auction.getId(),
        oldBidder.getId(),
        oldBidder.getUsername(),
        bidDTO.amount() + auction.getStepPrice(),
        Instant.now(),
        null
    );
    BidDTO maxBidForOldBidder = new BidDTO(
        auction.getId(),
        oldBidder.getId(),
        oldBidder.getUsername(),
        oldConfig.maxPrice,
        Instant.now(),
        null
    );
    // kiểm tra để tránh tự nâng giá bản thân
    if (auction.getCurrentHighestBidder().getId().equals(bidDTO.bidderId())){
      if ("MANUAL".equals(type)) {
        // trường hợp tự ngứa tay đặt thêm, nếu quá autobid thì sẽ tự tắt autobid
        if (bidDTO.amount() > oldConfig.maxPrice) {
          AutobidConfigDAO.getInstance().updateConfig(oldConfig.auctionId, oldConfig.userId, 0, 0.0);
          AutobidDTO data = AutobidConfigDAO.getInstance().getConfigByAuctionAndUser(oldConfig.auctionId, oldConfig.userId).convert();
          MessageProtocol messageProtocol = new MessageProtocol(
              "AUTOBID",
              data,
              "SUCCESS",
              "Bạn đã tắt autobid vì đã vượt giá autobid của mình"
          );
          ClientService.getInstance().sendTo(data.userId, messageProtocol);
        }
        updateAuctionAndAddBidHistory(oldSession, auction, new BidDTO(
            oldConfig.auctionId,
            oldConfig.userId,
            oldBidder.getUsername(),
            bidDTO.amount(),
            Instant.now(),
            null
        ));
      }
      return;
    }
    if (oldConfig.maxPrice < bidDTO.amount() + auction.getStepPrice()){
      // autobid không qua bước giá nên lấy người mới nhưng vẫn cần ghi nhận autobid đạt giá trần.
      updateAuctionAndAddBidHistory(oldSession, auction, maxBidForOldBidder);
      Auction newAuction = AuctionDAO.getInstance().getAuctionById(auction.getId());
      updateAuctionAndAddBidHistory(newSession, newAuction, bidDTO);
      // thông báo mức giá trần đã bị vượt
      maxPriceNoti(oldConfig.userId);
      return;

    }
    // nếu lớn hơn hẳn hoặc bằng thì lấy autobid nhưng vẫn cần ghi nhận bid mới.
    // thông báo bạn đã đạt mức trần nếu bằng
    updateAuctionAndAddBidHistory(newSession, auction, bidDTO);
    Auction newAuction = AuctionDAO.getInstance().getAuctionById(auction.getId());
    updateAuctionAndAddBidHistory(oldSession, newAuction, newBidForOldBidder);

  }
}
