package com.example.auction.database.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.auction.shared.dto.NotificationDTO;
import com.example.auction.shared.dto.PaymentDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO extends BaseDAO {
    private static final Logger log = LoggerFactory.getLogger(NotificationDAO.class);

    private static NotificationDAO instance;

    private NotificationDAO() {
    }

    public static NotificationDAO getInstance() {
        if (instance == null) {
            synchronized (BidDAO.class) {
                if (instance == null) {
                    instance = new NotificationDAO();
                }
            }
        }
        return instance;
    }

    public void addNotification(NotificationDTO notificationDTO) {
        String sql = "INSERT INTO notifications (user_id, title, message,type) VALUES (?,?,?,?)";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1,notificationDTO.getUserId());
            preparedStatement.setString(2,notificationDTO.getTitle());
            preparedStatement.setString(3,notificationDTO.getMessage());
            preparedStatement.setString(4,notificationDTO.getType());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error("Exception", e);
        }
        log.info("DA THEM THANH CONG 1 NOTI");
    }
    public List<NotificationDTO> getAllNotification(Long userId){
        String sql = "SELECT * FROM notifications WHERE user_id = ?";
        log.info("USER CUA USER LA: " + userId);
        List<NotificationDTO> list = new ArrayList<>();
        try (Connection connection = getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1,userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                NotificationDTO notificationDTO = new NotificationDTO(resultSet.getLong("id"),resultSet.getLong("user_id"),
                        resultSet.getString("title"),resultSet.getString("message"),resultSet.getString("type"),resultSet.getBoolean("is_read"),
                        resultSet.getLong("related_auction_id"),resultSet.getTimestamp("created_at").toInstant());
                list.add(notificationDTO);
            }
        }catch (Exception e){
            log.error("Exception", e);
        }
        log.info("DA LAY TAT CA THONG BAO THANH CONG");
        return list;
    }
}
