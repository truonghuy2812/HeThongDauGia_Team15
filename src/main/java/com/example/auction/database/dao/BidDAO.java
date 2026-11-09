package com.example.auction.database.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.auction.shared.dto.BidDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BidDAO extends BaseDAO {
    private static final Logger log = LoggerFactory.getLogger(BidDAO.class);

    private static BidDAO instance;

    private BidDAO() {
    }

    public static BidDAO getInstance() {
        if (instance == null) {
            synchronized (BidDAO.class) {
                if (instance == null) {
                    instance = new BidDAO();
                }
            }
        }
        return instance;
    }

    // Save bid vào database
    public void addBid(BidDTO bidDTO) {
        String sql = "INSERT INTO bids (auction_id, bidder_id, amount, bid_time, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, bidDTO.auctionId());
            ps.setLong(2, bidDTO.bidderId());
            ps.setDouble(3, bidDTO.amount());
            ps.setTimestamp(4, Timestamp.from(bidDTO.bidTime()));
            ps.setString(5, bidDTO.status());

            ps.executeUpdate();
            log.info("Bid saved: " + bidDTO.bidderId() + " bid " + bidDTO.amount());

        } catch (SQLException e) {
            log.error("Exception", e);
        }
    }

    public List<BidDTO> getAllBid(Long auctionId) {
        String sql = "SELECT * FROM bids WHERE auction_id = ?";
        List<BidDTO> bidDTOList = new ArrayList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, auctionId);
            log.info("{}", auctionId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String bidderName = UserDAO.getInstance().getUsernameByBidderId(resultSet.getLong("bidder_id"));
                bidDTOList.add(new BidDTO(resultSet.getLong("auction_id"), resultSet.getLong("bidder_id"), bidderName,
                        resultSet.getDouble("amount"), resultSet.getTimestamp("bid_time").toInstant(), "SUCCESS"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bidDTOList;
    }
}

