/*
package com.example.auction.server.service;

import com.example.auction.database.dao.*;
import com.example.auction.server.entity.Auction;
import com.example.auction.server.entity.AutobidConfig;
import com.example.auction.server.entity.Role;
import com.example.auction.server.entity.User;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.shared.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class BidService {
    private static final Logger log = LoggerFactory.getLogger(BidService.class);
    private final AuctionService auctionService = new AuctionService();

    // Đưa lock quản lý đồng bộ vào service để tái sử dụng ở mọi nơi
    private static final ConcurrentHashMap<Long, Object> auctionLocks = new ConcurrentHashMap<>();

    // Hàm chính, trả về PaymentAccountDTO để Handler gửi lại cho Client
    public PaymentAccountDTO processBid(BidDTO bidDTO, String type, UserSession userSession) throws Exception {
        User bidder = UserDAO.getInstance().getUserbyId(bidDTO.bidderId());
        validateRole(userSession, bidder);

        Object lock = auctionLocks.computeIfAbsent(bidDTO.auctionId(), _ -> new Object());

        synchronized (lock) {
            Auction auction = AuctionDAO.getInstance().getAuctionById(bidDTO.auctionId());
            validateBidRules(auction, bidDTO, type, bidder);

            AutobidConfig oldConfig = AutobidConfigDAO.getInstance().getFirstConfigByMaxPrice(bidDTO.auctionId());
            boolean oldConfigIsExisted = oldConfig.getUserId() != 0;
            boolean oldConfigIsLeader = oldConfig.getUserId() == auction.getCurrentHighestBidder().getId();

            if (oldConfigIsExisted && oldConfigIsLeader && !"AUTOBID".equals(type)) {
                handleAutobidVsManualBid(bidDTO, auction, oldConfig, type);
            } else {
                updateAuctionAndAddBidHistory(auction, bidDTO);
            }

            // Trả về thông tin tài khoản mới nhất sau khi trừ tiền để Handler phản hồi
            return PaymentAccountDAO.getInstance().getAccountByOwnerId(bidDTO.bidderId());
        }
    }

    private void validateRole(UserSession userSession, User user) throws IllegalArgumentException {
        if (userSession == null) throw new IllegalArgumentException("Phải login trước khi đấu giá");
        if (userSession.getRole() != Role.MEMBER) throw new IllegalArgumentException("Chỉ BIDDER hoặc SELLER mới được đấu giá");
        if (!user.getUsername().equals(userSession.getUserName())) throw new IllegalArgumentException("Username không trùng");
    }

    private void validateBidRules(Auction auction, BidDTO bidDTO, String type, User bidder) {
        if (!auction.getStatus().name().equals("ACTIVE")) {
            throw new IllegalArgumentException("Không chấp nhận vì phiên đã kết thúc hoặc chưa bắt đầu!");
        }
        if (bidDTO.amount() == null || bidDTO.amount() <= 0) {
            throw new IllegalArgumentException("Số tiền phải > 0");
        }
        if (auction.getCurrentHighestBid() + auction.getStepPrice() > bidDTO.amount() && !"AUTOBID".equals(type)) {
            throw new IllegalArgumentException("Giá đặt chưa đủ!");
        }
        if (bidDTO.amount() > bidder.getPaymentAccount().getBalance()) {
            throw new IllegalArgumentException("Số dư không đủ để đặt giá!");
        }
    }

    private void updateAuctionAndAddBidHistory(Auction auction, BidDTO bidDTO) {
        outbidHandle(auction.getId());
        AuctionDAO.getInstance().updateHighestBid(auction.getId(), bidDTO.amount(), bidDTO.bidderId());

        long timeLimit = Duration.between(Instant.now(), auction.getEndTime()).toSeconds();
        if (timeLimit <= 60) {
            auctionService.antiSniping(auction);
            AuctionDetailsDTO auctionDetailsDTO = new AuctionDetailsDTO(auction);
            ClientService.getInstance().broadcast(new MessageProtocol("ANTI_SNIPE", auctionDetailsDTO, "SUCCESS", "ANTI SNIPE"));
        }

        BidDAO.getInstance().addBid(bidDTO);
        log.info("Bid accepted: " + auction.getId() + " bid " + bidDTO.amount() + " for auction " + bidDTO.auctionId());

        // Trừ tiền
        PaymentAccountDTO before = PaymentAccountDAO.getInstance().getAccountByOwnerId(bidDTO.bidderId());
        PaymentAccountDAO.getInstance().updateAccountByOwnerId(before.ownerId(), before.balance() - bidDTO.amount(), null);

        broadcastNewBid(bidDTO);
    }

    private void handleAutobidVsManualBid(BidDTO bidDTO, Auction auction, AutobidConfig oldAutobidConfig, String type) throws SQLException {
        // [Toàn bộ logic của hàm handleAutobidVsManualBid cũ bê sang đây]
        // Lưu ý: Thay các đoạn gọi oldSession.sendResponse bằng ClientService.getInstance().sendTo(...)
    }

    private void outbidHandle(long auctionId) {
        // [Toàn bộ logic của hàm outbidHandle cũ bê sang đây]
        // Vì ClientService là một service thông báo toàn cục, BidService gọi ClientService để bắn thông báo là hợp lệ.
    }

    private void broadcastNewBid(BidDTO bidDTO) {
        MessageProtocol broadcastMsg = new MessageProtocol("NEW_BID", bidDTO, "SUCCESS", null);
        ClientService.getInstance().broadcast(broadcastMsg);

        PaymentDTO paymentDTO = new PaymentDTO(bidDTO.bidderId(), bidDTO.amount(), "BID", Instant.now(), null, "Bạn đã đấu giá thành công");
        PaymentRequestDAO.getInstance().createPaymentRequest(paymentDTO);
    }
}
*/
