package com.example.auction.server.servernetworkmanager;

import com.example.auction.server.entity.PaymentAccount;
import com.example.auction.server.entity.Role;

import java.io.Serializable;
import java.time.Instant;

/**
 * UserSession - Lưu thông tin user đã đăng nhập trên SERVER
 */
public class UserSession implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String userName, email;
    private Role role;
    private Instant loginTime;
    private PaymentAccount paymentAccount;

    // Constructor 4 tham số (giữ lại để các file khác trong project đang dùng không bị lỗi)
    public UserSession(Long id, String username, String email, Role role) {
        this(id, username, email, role, null);
    }

    // Constructor 5 tham số (khớp hoàn toàn với UserDAO của bạn)
    public UserSession(Long id, String username, String email, Role role, PaymentAccount paymentAccount) {
        this.id = id;
        this.userName = username;
        this.email = email;
        this.role = role;
        this.paymentAccount = paymentAccount;
        this.loginTime = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public PaymentAccount getPaymentAccount() {
        return paymentAccount;
    }

    public void setPaymentAccount(PaymentAccount paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    public long getSessionDurationSeconds() {
        return java.time.temporal.ChronoUnit.SECONDS.between(loginTime, Instant.now());
    }

    public Instant getLoginTime() {
        return loginTime;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "id=" + id +
                ", username='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", loginTime=" + loginTime +
                '}';
    }
}