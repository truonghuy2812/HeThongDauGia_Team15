package com.example.auction.database.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.auction.server.entity.PaymentAccount;
import com.example.auction.shared.dto.PaymentAccountDTO;

import java.sql.*;
import java.time.Instant;

public class PaymentAccountDAO extends BaseDAO {
    private static final Logger log = LoggerFactory.getLogger(PaymentAccountDAO.class);

    private static PaymentAccountDAO instance = null;

    private PaymentAccountDAO() {

    }
    public static PaymentAccountDAO getInstance() {
        if (instance == null) {
            synchronized (PaymentAccountDAO.class) {
                if (instance == null) {
                    instance = new PaymentAccountDAO();
                }
            }
        }
        return instance;
    }

    public void createPaymentAccountForOwner(long ownerId) {
        String sql = "INSERT INTO payment_accounts (owner_id, balance, frozen_balance) VALUES (?, 0, 0)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, ownerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Exception", e);
        }
    }

    /** @deprecated dùng {@link #createPaymentAccountForOwner(long)} */
    public void createPaymentAccount() {
        log.error("createPaymentAccount() không gắn owner_id — dùng createPaymentAccountForOwner");
    }

    public PaymentAccountDTO getAccountByOwnerId(long ownerId) {
        String sql = "SELECT * FROM payment_accounts WHERE owner_id = ?";
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PaymentAccountDTO(
                            ownerId,
                            rs.getDouble("balance"),
                            rs.getDouble("frozen_balance"),
                            Instant.now());
                }
            }
        } catch (SQLException e) {
            log.error("Exception", e);
        }
        return null;
    }

    public void updateAccountByOwnerId(long ownerId, Double balance, String status) {
        String sql = "UPDATE payment_accounts SET balance = ?," +
                "frozen_balance = ?," +
                "updated_at = ?" +
                "WHERE owner_id = ?";
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDouble(1, balance);
            ps.setDouble(2, 0.0);
            ps.setTimestamp(3, Timestamp.from(Instant.now()));
            ps.setLong(4, ownerId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                log.info("Cập nhật thành công!");
            } else {
                log.info("Có lỗi trong lúc cập nhật!");
            }
        } catch (SQLException e) {
            log.error("Exception", e);
        }

    }

}
