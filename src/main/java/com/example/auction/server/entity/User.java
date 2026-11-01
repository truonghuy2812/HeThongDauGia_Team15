package com.example.auction.server.entity;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Role {
        ADMIN, MEMBER, BIDDER, GUEST
    }

    private Long id;
    private String username;
    private String password;
    private String email;
    private Role role;

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    // bổ sung

    //hàm kiểm tra quyền đấu giá
    public boolean canBid() {
        // Chỉ cho phép đấu giá nếu có Role và không phải là GUEST
        return this.role != null && this.role != Role.GUEST;
    }

    //ghi đè equals và hashCode để so sánh User (bidder có trùng seller không)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}