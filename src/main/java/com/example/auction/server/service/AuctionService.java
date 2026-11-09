
package com.example.auction.server.service;

import com.example.auction.database.dao.AuctionDAO;
import com.example.auction.database.dao.PaymentAccountDAO;
import com.example.auction.database.dao.PaymentRequestDAO;
import com.example.auction.server.entity.Auction;
import com.example.auction.server.entity.AuctionStatus;
import com.example.auction.server.entity.PaymentAccount;
import com.example.auction.server.entity.User;
import com.example.auction.shared.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

/**
 * AuctionService xử lý logic nghiệp vụ cấp cao (Business Logic).
 * Chịu trách nhiệm xác thực thời gian thực và điều phối các trạng thái của phiên đấu giá.
 */
public class AuctionService {
    private static final Logger log = LoggerFactory.getLogger(AuctionService.class);

    private static final ConcurrentHashMap<Long, Future<?>> auctionFutures = new ConcurrentHashMap<>();

    private final AuctionDependencies deps;
    private final ScheduledExecutorService timerScheduled;
    private final ExecutorService executors;

    // Constructor production (giữ behavior cũ)
    public AuctionService() {
        this(new DefaultAuctionDependencies(),
          Executors.newScheduledThreadPool(4),
          Executors.newVirtualThreadPerTaskExecutor());
    }

    // Constructor cho test (inject)
    public AuctionService(AuctionDependencies deps,
                          ScheduledExecutorService timerScheduled,
                          ExecutorService executors) {
        this.deps = deps;
        this.timerScheduled = timerScheduled;
        this.executors = executors;
    }

    // Bắt đầu timer cho auction
    public void startAuctionTimer(Auction auction) {
        try {
            Instant endTime = auction.getEndTime();
            long delay = Duration.between(Instant.now(), endTime).toMillis();

            if (delay <= 0) {
                closeAuction(auction);
                log.info("Phiên đã quá hạn!");
                return;
            }

            ScheduledFuture<?> scheduledFuture = timerScheduled.schedule(() -> {
                executors.submit(() -> closeAuction(auction));
            }, delay, TimeUnit.MILLISECONDS);

            auctionFutures.put(auction.getId(), scheduledFuture);

        } catch (IllegalArgumentException e) {
            log.warn("{}", e.getMessage());
        }
    }

    // Đóng phiên đấu giá khi hết thời gian
    public void closeAuction(Auction auction) {
        try {
            if (!auction.hasEnded()) {
                throw new IllegalStateException("Chưa đến thời gian kết thúc");
            }

            auction.closeAuction();

            // 1. Kiểm tra và lấy ID người thắng (nếu có) để tránh lặp lại code và tránh lỗi NPE
            User highestBidder = auction.getCurrentHighestBidder();
            Long winnerId = (highestBidder != null) ? highestBidder.getId() : 0L;

            // 2. Cập nhật trạng thái đóng phiên vào DB (sử dụng winnerId an toàn)
            AuctionDAO.getInstance().closeAuctionbyId(auction.getId(), winnerId);
            deps.closeAuctionById(auction.getId(), winnerId);



            Auction auctionDB = deps.getAuctionById(auction.getId());
            AuctionDetailsDTO auctionDetailsDTO = new AuctionDetailsDTO(auctionDB);

            MessageProtocol messageProtocol =
              new MessageProtocol("COMPLETE_AUCTION", auctionDetailsDTO, "SUCCESS", "HOAN THANH");

            deps.broadcast(messageProtocol);

            log.info("AUCTION: " + auctionDB);
            log.info("BID COUNT: " + auctionDB.getBidCount());

            // Xử lý logic thắng cuộc hoặc hủy phiên dựa trên số lượt bid
            if (auctionDB.getBidCount() > 0) {
                log.info("Người thắng là: " + auction.getCurrentHighestBidder().getUsername());
                completeAuction(auctionDB);
            } else {
                log.info("Không có người thắng!");
                cancelAuction(auctionDB);
            }
        } catch (IllegalArgumentException e) {
            log.warn("{}", e.getMessage());
        }
    }

    // Hoàn tất phiên (sau khi thanh toán/giao hàng)
    public void completeAuction(Auction auction) {
        try {
            log.info("AUCION STATUS: " + auction.getStatus());
            if (auction.getStatus() != AuctionStatus.CLOSED) {
                throw new IllegalStateException("Chỉ hoàn tất phiên đã CLOSED");
            }
            auction.completeAuction();
            AuctionDAO.getInstance().updateAuctionById(auction.getId(), "COMPLETED");
            deps.updateAuctionStatus(auction.getId(), "COMPLETED");

            // Cộng tiền cho người bán
            User seller = auction.getSeller();
            PaymentAccount paymentAccount = seller.getPaymentAccount();
            paymentAccount.deposit(auction.getCurrentHighestBid());

            PaymentAccountDAO.getInstance().updateAccountByOwnerId(seller.getId(), paymentAccount.getBalance(), "SUCCESS");
            deps.updateAccountByOwnerId(
              seller.getId(),
              paymentAccount.getBalance(),
              paymentAccount.getFrozenBalance()
            );
            PaymentDTO paymentDTO = new PaymentDTO(seller.getId(), paymentAccount.getBalance(),"REMIT",Instant.now(),null,"Phiên đã kết thúc, tiền của bạn đây");
            PaymentRequestDAO.getInstance().createPaymentRequest(paymentDTO);
            PaymentAccountDTO paymentAccountDTO = seller.getPaymentAccount().convert();
            UserDTO userDTO = new UserDTO(seller.getId(),seller.getUsername(),null,seller.getEmail(),seller.getRole().name(),paymentAccountDTO);
            log.info("Đã thanh toán cho người bán!");
            MessageProtocol messageProtocol = new MessageProtocol("UPDATE_ACCOUNT",userDTO, "SUCCESS", "PHIEN HOAN THANH LEN TIEN");
            ClientService.getInstance().sendTo(seller.getId(),messageProtocol);

        } catch (IllegalArgumentException e) {
            log.warn("{}", e.getMessage());
        }
    }

    // Hủy phiên đấu giá nếu không ai bid
    public void cancelAuction(Auction auction) { // chỉ khi không có người đặt thì mới hủy
        try {
            // Không thể hủy nếu quy trình giao dịch cuối cùng đã xong
            if (auction.getStatus() == AuctionStatus.COMPLETED) {
                throw new IllegalStateException("Không thể hủy phiên đấu giá đã hoàn tất giao dịch");
            }
            auction.cancelAuction();
            log.info("Bắt đầu cập nhật DB");
            AuctionDAO.getInstance().updateAuctionById(auction.getId(), "CANCELED");
            deps.updateAuctionStatus(auction.getId(), "CANCELED");
            log.info("Completed!");
        } catch (IllegalArgumentException e) {
            log.warn("{}", e.getMessage());
        }
    }

    public void antiSniping(Auction auction) {

        Future<?> f = auctionFutures.get(auction.getId());
        if (f != null) {
            f.cancel(true);
            auctionFutures.remove(auction.getId());
        }
        Auction auction1 = new Auction(auction);
        long delay = Duration.between(Instant.now(), auction.getEndTime()).getSeconds() + 30;
        ScheduledFuture<?> scheduledFuture = timerScheduled.schedule(() -> {
            executors.submit(() -> closeAuction(auction1));
        }, delay, TimeUnit.SECONDS);

        auctionFutures.forEach((k,v) -> {
            log.info(k + " " + v);
        });
        auctionFutures.put(auction.getId(), scheduledFuture);

        Instant newEndTime = auction.getEndTime().plus(Duration.ofSeconds(30));
        AuctionDAO.getInstance().updateEndTimeById(auction.getId(), newEndTime);
        auction.setEndTime(newEndTime);
        deps.updateAuctionEndTime(auction.getId(), newEndTime);
    }
}
