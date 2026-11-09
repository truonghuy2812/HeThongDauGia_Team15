package com.example.auction.server.entity;

import java.time.Instant;
import java.util.Objects;

/**
 * Lớp Bid đại diện cho MỘT lần đặt giá trong một phiên đấu giá.
 */
public class Bid extends Entity {
    private Long auctionId;      // ID của buổi đấu giá
    private User bidder;         // Đối tượng người đặt giá
    private double amount;       // Số tiền đặt
    private Instant bidTime;     // Thời gian đặt giá
    private BidStatus status;    // Trạng thái bid (SUCCESS, FAILED, OUTBID, CANCELLED)

    public Bid(Long auctionId, User bidder, double amount) {
        this(auctionId, bidder, amount, Instant.now());
    }

    // Constructor đầy đủ (thường dùng cho Test hoặc Mapping từ DB)
    public Bid(Long auctionId, User bidder, double amount, Instant bidTime) {
        super();
        validateInitialData(auctionId , bidder , amount);
        this.auctionId = auctionId;
        this.bidder = bidder;
        this.amount = amount;
        this.bidTime = (bidTime != null) ? bidTime : Instant.now();
        this.status = BidStatus.SUCCESS;

    }

    private void validateInitialData(Long auctionId, User bidder, double amount) {
        if (auctionId == null || auctionId <= 0) {
            throw new IllegalArgumentException("AuctionId không hợp lệ");
        }
        if (bidder == null || !bidder.canBid()) {
            throw new IllegalArgumentException("Người đặt giá không hợp lệ hoặc không có quyền đấu giá");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền đặt giá phải lớn hơn 0");
        }
    }

    // Copy Constructor
    public Bid(Bid other) {
        super(other.getId());
        this.auctionId = other.auctionId;
        this.bidder = new User(other.bidder); // Defensive copy của User
        this.amount = other.amount;
        this.bidTime = other.bidTime;
        this.status = other.status;
        this.setCreatedAt(other.getCreatedAt());
        this.setUpdatedAt(other.getUpdatedAt());
    }

    // --- GETTERS ---
    public Long getAuctionId() { return auctionId; }
    public User getBidder() { return bidder; }
    public double getAmount() { return amount; }
    public Instant getBidTime() { return bidTime; }
    public BidStatus getStatus() { return status; }

    // --- STATE MANAGEMENT ---

    public void setStatus(BidStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status không được null");
        }
        this.status = status;
        this.setUpdatedAt(Instant.now());
    }

    /**
     * Đánh dấu bid bị OUTBID (khi có người khác đặt giá cao hơn).
     * Chỉ có thể chuyển từ SUCCESS sang OUTBID.
     */
    public void markOutbid() {
        if (this.status != BidStatus.SUCCESS) {
            throw new IllegalStateException("Chỉ bid đang SUCCESS mới có thể bị OUTBID");
        }
        this.status = BidStatus.OUTBID;
        this.setUpdatedAt(Instant.now());
    }

    /**
     * Đánh dấu bid thất bại (ví dụ: lỗi hệ thống, không đủ số dư ví).
     */
    public void markAsFailed() {
        this.status = BidStatus.FAILED;
        this.setUpdatedAt(Instant.now());
    }

    /**
     * Đánh dấu bid bị hủy (ví dụ: người dùng rút lại bid nếu quy định cho phép).
     */
    public void markCancelled() {
        this.status = BidStatus.CANCELLED;
        this.setUpdatedAt(Instant.now());
    }

    public boolean isActive() {
        return this.status == BidStatus.SUCCESS;
    }

    public boolean isSuccessful() {
        return this.status == BidStatus.SUCCESS;
    }

    // --- OVERRIDES ---

    @Override
    public String toString() {
        return String.format("Bid{id=%d, auctionId=%d, bidder=%s, amount=%.2f, status=%s}",
                getId(), auctionId, bidder.getUsername(), amount, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bid)) return false;
        Bid other = (Bid) o;
        // Sử dụng ID từ lớp cha Entity để so sánh tính duy nhất
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}