package com.example.auction.server.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Auction extends Entity {
    private Item item;
    private User seller;
    private AuctionStatus status;
    private Instant startTime;
    private Instant endTime;
    private int bidCount;
    private double currentHighestBid;
    private User currentHighestBidder;
    private User winner;
    private double startPrice;
    private double stepPrice;
    private List<Bid> bidHistory;
    // QUAN TRỌNG: timeProvider để inject thời gian
    // Lý do cần timeProvider:
    // Production: Supplier = Instant::now (dùng thời gian thực)
    // Test: Supplier = () -> FIXED_TIME (dùng thời gian cố định)
    // Nếu không có timeProvider:
    // Test sẽ phụ thuộc vào thời gian hệ thống
    // Test result sẽ không deterministic (không ổn định)
    // Cùng test code, chạy lần này pass, lần khác fail
    // Với timeProvider:
    // Test có thể kiểm soát thời gian
    // Kết quả luôn consistent
    // Supplier để inject thời gian (cho test)
    private Supplier<Instant> timeProvider;

    public Auction(Item item, User seller, Instant start, Instant end, double price, double step, User currentHighestBidder, double currentHighestBid, AuctionStatus status) {
        this(item, seller, start, end, price, step, Instant::now , currentHighestBidder , currentHighestBid);
        this.status = status;
    }

    // Constructor hỗ trợ Test Injection
    public Auction(Item item, User seller, Instant startTime, Instant endTime, double startPrice, double stepPrice, Supplier<Instant> timeProvider,User currentHighestBidder, double currentHighestBid) {
        super();
        validateInitialData(item , seller , startTime , endTime , startPrice);
        this.bidCount = 0;
        this.timeProvider = (timeProvider != null) ? timeProvider : Instant::now;
        this.item = item;
        this.seller = seller;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startPrice = startPrice;
        this.stepPrice = stepPrice;
        this.status = AuctionStatus.PENDING;
        this.currentHighestBid = currentHighestBid;
        this.currentHighestBidder = currentHighestBidder;
        this.bidHistory = new ArrayList<>();
    }

    private void validateInitialData(Item item, User seller, Instant start, Instant end, double price) {
        if (end.isBefore(start)) throw new IllegalArgumentException("Thời gian kết thúc phải sau thời gian bắt đầu");
        if (price < 0) throw new IllegalArgumentException("Giá khởi điểm không được âm");
        if (seller == null) {
            throw new IllegalArgumentException("Người bán không hợp lệ");
        }
        if (item == null || start == null || end == null) {
            throw new IllegalArgumentException("Thông tin đấu giá không được để trống");
        }
    }
    //copy constructor
    public Auction(Auction other) {
        super(other.getId(), other.getCreatedAt(), other.getUpdatedAt());
        this.item = other.item;
        this.seller = other.seller;
        this.status = other.status;
        this.startTime = other.startTime;
        this.endTime = other.endTime;
        this.currentHighestBid = other.currentHighestBid;
        this.currentHighestBidder = other.currentHighestBidder;
        this.winner = other.winner;
        this.startPrice = other.startPrice;
        this.stepPrice = other.stepPrice;
        this.bidHistory = new ArrayList<>(other.bidHistory);
        this.timeProvider = other.timeProvider;
    }

    // --- GETTERS ---
    public Item getItem() { return item; }
    public User getSeller() { return seller; }
    public AuctionStatus getStatus() { return status; }
    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
    public double getCurrentHighestBid() { return currentHighestBid; }
    public User getCurrentHighestBidder() { return currentHighestBidder; }
    public User getWinner() { return winner; }
    public double getStartPrice() { return startPrice; }
    public double getStepPrice() { return stepPrice; }
    public int getBidCount() { return  bidCount; }

    // --- SETTERS ---

    public void setStartPrice(double startPrice) {
        if (startPrice <= 0) {
            throw new IllegalArgumentException("Start price phải > 0");
        }
        this.startPrice = startPrice;
        this.setUpdatedAt(java.time.Instant.now());
    }

    public void setSeller(User seller) {
        this.seller = seller;
        this.setUpdatedAt(java.time.Instant.now());
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
        this.setUpdatedAt(java.time.Instant.now());
    }


    public List<Bid> getBidHistory() {
        return new ArrayList<>(bidHistory); // Defensive copy
    }

    // --- LOGIC METHODS ---

    public boolean hasStarted() {
        return timeProvider.get().isAfter(startTime);
    }

    public boolean isOngoing() {
        Instant now = timeProvider.get();
        return status == AuctionStatus.ACTIVE && now.isAfter(startTime) && now.isBefore(endTime);
    }

    public boolean hasEnded() {
//        return timeProvider.get().isAfter(endTime);
        Instant now = timeProvider.get();
        // Thay vì chỉ isAfter, hãy chấp nhận cả trường hợp bằng (equals)
        return now.isAfter(endTime) || now.equals(endTime);
    }

    public long getTimeRemainingSeconds() {
        Instant now = timeProvider.get();
        if (now.isAfter(endTime)) return 0;
        return java.time.Duration.between(now, endTime).getSeconds();
    }

    public boolean addBid(Bid bid) {
        if (status != AuctionStatus.ACTIVE) return false;

        // Kiểm tra bước giá
        double minBid = bidHistory.isEmpty() ? startPrice : currentHighestBid + stepPrice;
        if (bid.getAmount() < minBid) return false;

        // Chặn người bán tự đấu giá hoặc người dùng không có quyền đấu giá
        User bidder = bid.getBidder();
        if (bidder.equals(seller) || !bidder.canBid()) return false;

        this.currentHighestBid = bid.getAmount();
        this.currentHighestBidder = bidder;
        this.bidHistory.add(bid);
        this.setUpdatedAt(timeProvider.get());
        return true;
    }


    public void closeAuction() {
        if (status != AuctionStatus.ACTIVE && status != AuctionStatus.PENDING) {
            throw new IllegalStateException("Phiên đấu giá không ở trạng thái có thể đóng");
        }

        if (!bidHistory.isEmpty()) {
            this.winner = currentHighestBidder;
        }
        this.status = AuctionStatus.CLOSED;
        this.setUpdatedAt(timeProvider.get());
    }

    public void completeAuction() {
        if (status != AuctionStatus.CLOSED) {
            throw new IllegalStateException("Chỉ có thể hoàn tất phiên đã CLOSED");
        }
        this.status = AuctionStatus.COMPLETED;
        this.setUpdatedAt(timeProvider.get());
    }

    public void cancelAuction() {
        if (status == AuctionStatus.COMPLETED) {
            throw new IllegalStateException("Không thể hủy phiên đã hoàn tất giao dịch");
        }
        this.status = AuctionStatus.CANCELED;
        this.setUpdatedAt(timeProvider.get());
    }

    public boolean isSellerOf(User user) {
        return seller.equals(user);
    }

    public boolean hasWinner() {
        return winner != null;
    }

    @Override
    public String toString() {
        return String.format("Auction{id=%s, item=%s, status=%s, highestBid=%.2f, winner=%s}",
                getId(), item.getItemName(), status, currentHighestBid,
                winner != null ? winner.getUsername() : "None");
    }

    public void setBidCount(int bidCount) {
        this.bidCount = bidCount;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
}