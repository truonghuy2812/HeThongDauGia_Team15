package com.example.auction.database.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.auction.shared.dto.PaymentDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentRequestDAO extends BaseDAO {
    private static final Logger log = LoggerFactory.getLogger(PaymentRequestDAO.class);

    private static PaymentRequestDAO instance = null;

    private PaymentRequestDAO() {
    }

    public static PaymentRequestDAO getInstance() {
        if (instance == null) {
            synchronized (PaymentRequestDAO.class) {
                if (instance == null) {
                    instance = new PaymentRequestDAO();
                }
            }
        }
        return instance;
    }

    public Long createPaymentRequest(PaymentDTO paymentDTO) {
        String sql = "INSERT INTO payment_request (owner_id, amount, time, status) VALUES (?,?,?,?)";

        // Thêm Statement.RETURN_GENERATED_KEYS ở đây
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, paymentDTO.ownerId());
            preparedStatement.setDouble(2, paymentDTO.amount());
            preparedStatement.setTimestamp(3, Timestamp.from(paymentDTO.time()));
            preparedStatement.setString(4, paymentDTO.status());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        log.info("UPDATE PAYMENT REQUEST THANH CONG - ID: " + id);
                        return id;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception", e);
        }
        return null;
    }

    public List<PaymentDTO> getAllPendingPaymentRequest() {
        String sql = "SELECT * FROM payment_request WHERE status = 'PENDING'";
        List<PaymentDTO> paymentDTOList = new ArrayList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                PaymentDTO paymentDTO = new PaymentDTO(resultSet.getLong(2), resultSet.
                        getDouble(3), "PENDING", resultSet.getTimestamp(4).toInstant(), resultSet.getLong(1), null);
                paymentDTOList.add(paymentDTO);
            }
            log.info("GET ALL PENDING PAYMENT REQUEST THANH CONG");
            return paymentDTOList;
        } catch (Exception e) {
            log.error("Exception", e);
        }
        return null;
    }

    public void approvePaymentRequest(Long paymentRequestId) {
        String sql = "UPDATE payment_request SET status = 'APPROVE' WHERE id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, paymentRequestId);
            preparedStatement.executeUpdate();
            log.info("APPROVE PAYMENT REQUEST THANH CONG");
        } catch (Exception e) {
            log.error("Exception", e);
        }
    }

    public List<PaymentDTO> getAllRequest(Long ownerId) {
        String sql = "SELECT * FROM payment_request WHERE owner_id = ?";
        List<PaymentDTO> paymentDTOList = new ArrayList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, ownerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String message = "";
                String status = resultSet.getString(5);
                switch (status) {
                    case "PENDING":
                        message = "ADMIN đang duyệt yêu cầu";
                        break;
                    case "APPROVE":
                        message = "ADMIN đã duyệt yêu cầu";
                        break;
                    case "BID":
                        message = "Bạn đặt giá";
                        break;
                    case "OUT_BID":
                        message = "Bạn đã bị trả giá cao hơn";
                        break;
                    case "REMIT":
                        message = "Phiên kết thúc, bạn được trả tiền";
                        break;
                    default:
                        message = "REQUEST KHONG TON TAI";
                        break;
                }
                PaymentDTO paymentDTO = new PaymentDTO(resultSet.getLong(1), resultSet.
                        getDouble(3), status, resultSet.getTimestamp(4).toInstant(), resultSet.getLong(1), message);
                paymentDTOList.add(paymentDTO);
            }
            return paymentDTOList;
        } catch (Exception e) {
            log.error("Exception", e);
        }
        return null;
    }
}

