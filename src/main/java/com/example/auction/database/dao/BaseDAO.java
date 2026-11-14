package com.example.auction.database.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseDAO {
    // kết nối đến database bằng root user
    // cac DAO sau buoc phai ke thua
    static String URL = "jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/auction?sslMode=VERIFY_IDENTITY";
    static String USERNAME = "2WJgi1mtyGjAL3s.root";
    static String PASSWORD = "jV0Q0VHTWb9vCT8e";
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
