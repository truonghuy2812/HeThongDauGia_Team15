package com.example.auction.server.servernetworkmanager;

import java.io.Serializable;
import java.time.Instant;

/**
 * UserSession - Lưu thông tin user đã đăng nhập trên SERVER
 */
public class UserSession implements Serializable {
    private static final long serialVersionUID = 1L;

    // Định nghĩa Role trực tiếp ở đây để tránh lỗi import
    public enum Role {
        ADMIN, MEMBER, BIDDER, GUEST
    }

    private Long id;
    private String userName, email;
    private Role role;
    private Instant loginTime;

    public UserSession(Long id, String username, String email, Role role) {
        this.id = id;
        this.userName = username;
        this.email = email;
        this.role = role;
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