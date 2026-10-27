package com.example.auction.database.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseDAO {
    // kết nối đến database bằng root user
    // cac DAO sau buoc phai ke thua
    static String URL = "jdbc:mysql://localhost:3306/auction?useSSL=false&serverTimezone=UTC";
    static String USERNAME = "root";
    static String PASSWORD = "1234";

    // Method để lấy Connection (kết nối database)
    // Các DAO con gọi method này
    protected static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    // Hàm test kết nối trực tiếp
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✅ Kết nối Database auction thành công!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi kết nối: " + e.getMessage());
        }
    }
}
