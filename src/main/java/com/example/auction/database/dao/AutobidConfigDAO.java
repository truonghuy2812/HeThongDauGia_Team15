package com.example.auction.database.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.auction.server.entity.AutobidConfig;
import com.example.auction.shared.dto.AutobidDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AutobidConfigDAO extends BaseDAO{
    private static final Logger log = LoggerFactory.getLogger(AutobidConfigDAO.class);

    public static volatile AutobidConfigDAO instance = null;
    private AutobidConfigDAO(){

    }
    public static AutobidConfigDAO getInstance(){
        if (instance == null){
            synchronized (AutobidConfigDAO.class){
                if (instance == null){
                    instance = new AutobidConfigDAO();
                }
            }
        }
        return instance;
    }
    private boolean checkIfConfigIsExisted(long auctionId, long userId){
        String sql = "SELECT 1 FROM auction_autobid_config WHERE auction_id = ? AND user_id = ?";
        try(Connection con = getConnection()){
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, auctionId);
            ps.setLong(2, userId);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch (SQLException e){
            log.error("Exception", e);
            return false;
        }
    }
    private void addNewConfig(long auctionId, long userId, int isOn, double maxPrice){
        String sql = "INSERT INTO auction_autobid_config (auction_id , user_id , is_on , max_price) VALUES (?, ?, ?, ?)";
        try(Connection con = getConnection()){
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1 , auctionId);
            ps.setLong(2, userId);
            ps.setInt(3, isOn);
            ps.setDouble(4, maxPrice);
            int row = ps.executeUpdate();
            if (row > 0){
                log.info("Thêm autobid mới thành công");
            }else{
                log.info("Thêm thất bại");
            }
        }catch (SQLException e){
            log.error("Exception", e);
        }
    }
    public void updateConfig(long auctionId, long userId, int isOn , double maxPrice) throws SQLException{
        //check xem tồn tại chưa
        if (!checkIfConfigIsExisted(auctionId, userId)){
            addNewConfig(auctionId, userId, isOn, maxPrice);
        }
        else{
            String sql = "UPDATE auction_autobid_config SET is_on = ? , max_price = ?, updated_at = ? WHERE auction_id = ? AND user_id = ?";
            try(Connection con = getConnection()){
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, isOn);
                ps.setDouble(2, maxPrice);
                ps.setTimestamp(3, Timestamp.from(Instant.now()));
                ps.setLong(4, auctionId);
                ps.setLong(5, userId);
                int row = ps.executeUpdate();
                if (row > 0){
                    log.info("Cập nhật autobid thành công!");
                }else {
                    log.info("Cập nhật autobid thất bại");
                }
            }
        }
    }
    public AutobidConfig getConfigByAuctionAndUser(long auctionId, long userId) throws SQLException{
        String sql = "SELECT * FROM auction_autobid_config WHERE auction_id = ? AND user_id = ?";
        AutobidConfig autobidConfig = new AutobidConfig();
        try(Connection con = getConnection()){
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, auctionId);
            ps.setLong(2, userId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    autobidConfig = new AutobidConfig(
                            1L,
                            rs.getTimestamp("created_at").toInstant(),
                            rs.getTimestamp("updated_at").toInstant(),
                            rs.getLong("auction_id"),
                            rs.getLong("user_id"),
                            rs.getInt("is_on"),
                            rs.getDouble("max_price")
                    );
                }
            }
        }
        return autobidConfig;
    }
    public AutobidConfig getFirstConfigByMaxPrice(long auctionId) throws SQLException{
        String sql = "SELECT * FROM auction_autobid_config" +
                " WHERE auction_id = ? AND is_on = 1" +
                " ORDER BY max_price DESC, created_at ASC" +
                " LIMIT 1";
        AutobidConfig autobidConfig = new AutobidConfig();
        try(Connection con = getConnection()){
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, auctionId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    autobidConfig = new AutobidConfig(
                            1L,
                            rs.getTimestamp("created_at").toInstant(),
                            rs.getTimestamp("updated_at").toInstant(),
                            auctionId,
                            rs.getLong("user_id"),
                            1,
                            rs.getDouble("max_price")
                    );
                }
            }
        }
        return autobidConfig;
    }
}
