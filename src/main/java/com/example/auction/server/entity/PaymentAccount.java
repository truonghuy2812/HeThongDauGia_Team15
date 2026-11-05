package com.example.auction.server.entity;
import com.example.auction.shared.dto.PaymentAccountDTO;

import java.sql.Timestamp;
import java.time.Instant;

public class PaymentAccount {
    private Long userId; // Liên kết 1:1 hoặc 1:N với lớp User (Khóa ngoại)
    private double balance; // Số dư khả dụng
    private double frozenBalance; // Số dư bị đóng băng khi đang giữ giá đấu
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructor không tham số (Bắt buộc cho các framework hoặc JDBC mapping)
    public PaymentAccount() {
        this.balance = 0;
        this.frozenBalance = 0;
    }
    public PaymentAccount(PaymentAccountDTO paymentAccountDTO){
        this.userId = paymentAccountDTO.ownerId();
        this.balance = paymentAccountDTO.balance();
        this.frozenBalance = paymentAccountDTO.frozenBalance();
        this.updatedAt = Timestamp.from(Instant.now());
    }
    // Constructor đầy đủ tham số khi khởi tạo tài khoản mới
    public PaymentAccount(Long id, Long userId, double balance) {
        this.userId = userId;
        this.balance = balance;
        this.frozenBalance = 0;
        this.createdAt = Timestamp.from(Instant.now());
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public PaymentAccount(Long id, Long userId, double balance, double frozenBalance) {
        this.userId = userId;
        this.balance = balance;
        this.frozenBalance = frozenBalance;
    }

    public PaymentAccountDTO convert(){
        return new PaymentAccountDTO(userId , balance, frozenBalance, Instant.now());
    }

    // ==========================================
    // LOGIC NGHIỆP VỤ ĐẤU GIÁ (BUSINESS LOGIC)
    // ==========================================

    /**
     * Nạp tiền vào tài khoản
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền nạp phải lớn hơn 0");
        }
        this.balance += amount;
        this.updatedAt = Timestamp.from(Instant.now()); // Cập nhật thời gian biến động
    }

    /**
     * Đóng băng tiền khi người dùng bấm đặt giá (Bid) một sản phẩm
     */
    public void freezeMoney(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền đóng băng phải lớn hơn 0");
        }
        if (balance < amount) {
            throw new IllegalStateException("Số dư khả dụng không đủ để tham gia đấu giá");
        }
        // Trừ tiền ở số dư khả dụng, cộng vào số dư đóng băng
        this.balance -= amount;
        this.frozenBalance += amount;
        this.updatedAt = Timestamp.from(Instant.now());
    }

    /**
     * Giải phóng tiền đóng băng khi có người khác đặt giá cao hơn (Trả lại tiền)
     */
    public void unfreezeMoney(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền giải phóng phải lớn hơn 0");
        }
        if (frozenBalance < amount) {
            throw new IllegalStateException("Số tiền muốn giải phóng lớn hơn số tiền đang bị đóng băng");
        }
        // Trừ ở ví đóng băng, trả lại ví khả dụng
        this.frozenBalance -= amount;
        this.balance += amount;
        this.updatedAt = Timestamp.from(Instant.now());
    }

    /**
     * Trừ hẳn tiền đóng băng khi người dùng trúng đấu giá thành công (Thực thu)
     */
    public void deductFrozenMoney(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền trừ phải lớn hơn 0");
        }
        if (frozenBalance - amount < 0) {
            throw new IllegalStateException("Số tiền đóng băng không đủ để thực hiện thanh toán");
        }
        // Khấu trừ thẳng từ ví đóng băng đi
        this.frozenBalance -= amount;
        this.updatedAt = Timestamp.from(Instant.now());
    }

    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public double getBalance() { return balance; }
    // Không nên có setBalance(BigDecimal) tự do để tránh việc sửa đổi số dư ngoài ý muốn,
    // thay vào đó hãy dùng deposit() hoặc freezeMoney()

    public double getFrozenBalance() { return frozenBalance; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "PaymentAccount{" +
                ", userId=" + userId +
                ", balance=" + balance +
                ", frozenBalance=" + frozenBalance +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}