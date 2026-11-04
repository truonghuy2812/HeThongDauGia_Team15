package com.example.auction.client.controller; // Hoặc package utils tùy bạn chia
public class SessionManager {
    // Biến lưu trữ tên người dùng (nếu null nghĩa là chưa đăng nhập)
    private static String currentUser = null;
    // Kiểm tra xem đã đăng nhập chưa
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    // Hàm gọi khi đăng nhập thành công
    public static void login(String username) {
        currentUser = username;
    }
    // Hàm gọi khi đăng xuất
    public static void logout() {
        currentUser = null;
    }
    // Lấy tên người dùng hiện tại
    public static String getCurrentUser() {
        return currentUser;
    }
}