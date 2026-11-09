package com.example.auction.database.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.example.auction.server.entity.Auction;
import com.example.auction.server.entity.AuctionStatus;
import com.example.auction.server.entity.Item;
import com.example.auction.server.entity.PaymentAccount;
import com.example.auction.server.entity.User;
import com.example.auction.shared.dto.AuctionDTO;
import com.example.auction.shared.dto.AuctionDetailsDTO;
import com.example.auction.shared.dto.AuctionPendingDTO;
import com.example.auction.shared.dto.ItemDTO;
import com.example.auction.shared.dto.PaymentAccountDTO;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AuctionDAO extends BaseDAO {
    private static final Logger log = LoggerFactory.getLogger(AuctionDAO.class);

    private static volatile AuctionDAO instance;

    private AuctionDAO() {
    }

    public static AuctionDAO getInstance() {
        if (instance == null) {
            synchronized (AuctionDAO.class) {
                if (instance == null) {
                    instance = new AuctionDAO();
                }
            }
        }
        return instance;
    }

    // Lấy auction đang active
    public List<Auction> getAllCurrentActiveAuction() {
        String sql = "SELECT * FROM auctions WHERE status = ?";
        List<Auction> res = new ArrayList<>();
        Auction auction = null;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "ACTIVE");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Item item = ItemDAO.getInstance().getItembyId(rs.getLong("item_id"));
                    User seller = UserDAO.getInstance().getUserbyId(rs.getLong("seller_id"));
                    User currentHighestBidder = UserDAO.getInstance().getUserbyId(rs.getLong("current_highest_bidder_id"));
                    PaymentAccountDTO paymentAccountDTO = PaymentAccountDAO.getInstance().getAccountByOwnerId(seller.getId());
                    PaymentAccount paymentAccount = new PaymentAccount(paymentAccountDTO);
                    seller.setPaymentAccount(paymentAccount);
                    //Item item, User seller, Instant start, Instant end, double price, double step, User currentHighestBidder, double currentHighestBid
                    auction = new Auction(
                            item,
                            seller,
                            rs.getTimestamp("start_time").toInstant(),
                            rs.getTimestamp("end_time").toInstant(),
                            rs.getDouble("start_price"),
                            rs.getDouble("step_price"),
                            currentHighestBidder,
                            rs.getDouble("current_highest_bid"),
                            AuctionStatus.valueOf(rs.getString("status"))
                    );
                    auction.setId(rs.getLong("id"));
                    auction.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
                    res.add(auction);
                }
            }
        } catch (SQLException e) {
            log.error("Exception", e);
        }
        return res;
    }

    // Lấy auction theo ID
    public Auction getAuctionById(Long auctionId) {
        String sql = "SELECT * FROM auctions WHERE id = ?";
        Auction auction = null;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, auctionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Item item = ItemDAO.getInstance().getItembyId(rs.getLong("item_id"));
                    User seller = UserDAO.getInstance().getUserbyId(rs.getLong("seller_id"));
                    PaymentAccountDTO paymentAccountDTO = PaymentAccountDAO.getInstance().getAccountByOwnerId(seller.getId());
                    PaymentAccount paymentAccount = new PaymentAccount(paymentAccountDTO);
                    seller.setPaymentAccount(paymentAccount);
                    User currentHighestBidder = UserDAO.getInstance().getUserbyId(rs.getLong("current_highest_bidder_id"));

                    //Item item, User seller, Instant start, Instant end, double price, double step, User currentHighestBidder, double currentHighestBid
                    auction = new Auction(
                            item,
                            seller,
                            rs.getTimestamp("start_time").toInstant(),
                            rs.getTimestamp("end_time").toInstant(),
                            rs.getDouble("start_price"),
                            rs.getDouble("step_price"),
                            currentHighestBidder,
                            rs.getDouble("current_highest_bid"),
                            AuctionStatus.valueOf(rs.getString("status"))
                    );
                    auction.setId(auctionId);
                    auction.setBidCount(rs.getInt("bid_count"));
                    auction.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
                    log.info("Auction found: " + auctionId);
                }
            }
        } catch (SQLException e) {
            log.error("Exception", e);
            return null;
        }
        log.info("AUCTION STATUS: " + auction.getStatus() + " AUCTION ID: " + auctionId);
        return auction;
    }


    // Update highest bid
    public void updateHighestBid(Long auctionId, Double amount, Long bidderId) {
        String sql = "UPDATE auctions SET current_highest_bid = ?, " +
                "current_highest_bidder_id = ?, " +
                "bid_count = bid_count + 1, " +"" +
                "updated_at = ?"+
                "WHERE id = ?;";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setLong(2, bidderId);
            ps.setTimestamp(3,Timestamp.from(Instant.now()));
            ps.setLong(4, auctionId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                log.info("Auction updated: bid=" + amount + ", bidderId=" + bidderId);
            }

        } catch (SQLException e) {
            log.error("Exception", e);
        }
    }


    public long addAuctionAndGetID(AuctionDetailsDTO auctionDTO) {
        String sql = "INSERT INTO auctions (item_id, start_price, step_price, current_highest_bid, seller_id, start_time, end_time, status, bid_count) VALUES (?,?,?,?,?,?,?,?,?)";
        long generatedId = -1;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setObject(1, auctionDTO.itemId, java.sql.Types.BIGINT);
            preparedStatement.setDouble(2, auctionDTO.startPrice);
            preparedStatement.setDouble(3, auctionDTO.stepPrice);
            preparedStatement.setDouble(4, auctionDTO.currentHighestBid);
            preparedStatement.setObject(5, auctionDTO.sellerId, java.sql.Types.BIGINT);

            // Kiểm tra null an toàn cho các trường thời gian tránh lỗi NullPointerException
            preparedStatement.setTimestamp(6, auctionDTO.startTime != null ? Timestamp.from(auctionDTO.startTime) : null);
            preparedStatement.setTimestamp(7, auctionDTO.endTime != null ? Timestamp.from(auctionDTO.endTime) : null);

            preparedStatement.setString(8, auctionDTO.status);
            preparedStatement.setInt(9, auctionDTO.bidCount);

            preparedStatement.executeUpdate();
            log.info("INSERT AUCTION THANH CONG");

            // Lấy ID tự động tăng (Auto Increment) vừa được tạo ra từ MySQL
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getLong(1);
                }
            }

        } catch (Exception e) {
            log.error("Exception", e);
        }

        return generatedId; // Trả ID thực tế về cho Handler
    }

    public List<AuctionPendingDTO> getAllPendingAuctions() {
        List<AuctionPendingDTO> list = new ArrayList<>();
        // Sử dụng INNER JOIN để lấy tên sản phẩm từ bảng item_table
        // và lấy tên người đăng từ bảng usertable một cách chính xác
        String sql = "SELECT a.id, i.item_name, u.username AS owner_name, a.start_price, a.status " +
                "FROM auctions a " +
                "LEFT JOIN item_table i ON a.item_id = i.item_id " +
                "LEFT JOIN usertable u ON a.seller_id = u.id " +
                "WHERE a.status = 'PENDING' " +
                "ORDER BY a.created_at DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Xử lý chống Null nếu LEFT JOIN không tìm thấy tên tương ứng
                String itemName = rs.getString("item_name") != null ? rs.getString("item_name") : "Sản phẩm ẩn";
                String ownerName = rs.getString("owner_name") != null ? rs.getString("owner_name") : "Vô danh";
                // Khởi tạo DTO bằng các nhãn cột đã được định nghĩa trong câu lệnh SELECT ở trên
                AuctionPendingDTO dto = new AuctionPendingDTO(
                        rs.getLong("id"),
                        rs.getString("item_name"),  // Khớp với i.item_name
                        rs.getString("owner_name"), // Khớp với u.username AS owner_name
                        rs.getDouble("start_price"),
                        rs.getString("status")
                );
                list.add(dto);
            }
            log.info("====== SERVER LOG ======");
            log.info("Đã tìm thấy và gửi " + list.size() + " phiên PENDING cho Admin.");
        } catch (SQLException e) {
            log.error("Lỗi nghiêm trọng tại getAllPendingAuctions: " + e.getMessage());
            log.error("Exception", e);
        }
        return list;
    }

    public List<AuctionDetailsDTO> getAllActiveAuctions() {
        List<AuctionDetailsDTO> list = new ArrayList<>();

        // truy vấn các sp đang có phiên đấu giá đang ACTIVE hoặc PENDING
        String sql = "SELECT " +
                "id, " +
                "start_price, " +
                "step_price, " +
                "current_highest_bid, " +
                "current_highest_bidder_id, " +
                "seller_id, " +
                "item_id, " +
                "start_time, " +
                "end_time, " +
                "status, " +
                "bid_count " +
                "FROM auctions " +
                "WHERE status IN ('ACTIVE') " +
                "ORDER BY created_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(buildAuctionDetailsFromResultSet(rs));
            }
            log.info("Loaded " + list.size() + " active auctions");
        } catch (SQLException e) {
            log.error("Exception", e);
        }
        return list;
    }

    /**
     * Tìm phiên ACTIVE theo tên SP, loại (tag), mô tả, tên người bán.
     */
    public List<AuctionDetailsDTO> searchActiveAuctions(String keyword) {
        List<AuctionDetailsDTO> list = new ArrayList<>();
        if (keyword == null || keyword.isBlank()) {
            return getAllActiveAuctions();
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        String sql = "SELECT " +
                "a.id, " +
                "a.start_price, " +
                "a.step_price, " +
                "a.current_highest_bid, " +
                "a.current_highest_bidder_id, " +
                "a.seller_id, " +
                "a.item_id, " +
                "a.start_time, " +
                "a.end_time, " +
                "a.status, " +
                "a.bid_count " +
                "FROM auctions a " +
                "INNER JOIN item_table i ON a.item_id = i.item_id " +
                "INNER JOIN usertable u ON a.seller_id = u.id " +
                "WHERE a.status = 'ACTIVE' " +
                "AND (LOWER(i.item_name) LIKE ? " +
                "OR LOWER(i.tag) LIKE ? " +
                "OR LOWER(COALESCE(i.info, '')) LIKE ? " +
                "OR LOWER(u.username) LIKE ?) " +
                "ORDER BY a.created_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildAuctionDetailsFromResultSet(rs));
                }
            }
            log.info("Search \"" + keyword + "\": " + list.size() + " auction(s)");
        } catch (SQLException e) {
            log.error("Exception", e);
        }
        return list;
    }

    private AuctionDetailsDTO buildAuctionDetailsFromResultSet(ResultSet rs) throws SQLException {
        long itemId = rs.getLong("item_id");
        Item item = ItemDAO.getInstance().getItembyId(itemId);
        long sellerId = rs.getLong("seller_id");
        User seller = UserDAO.getInstance().getUserbyId(sellerId);
        long currentHighestBidderId = rs.getLong("current_highest_bidder_id");
        String highestBidderName = "Chưa có";
        if (currentHighestBidderId != 0) {
            User highestBidder = UserDAO.getInstance().getUserbyId(currentHighestBidderId);
            if (highestBidder != null) {
                highestBidderName = highestBidder.getUsername();
            }
        }
        AuctionDTO auctionDTO = new AuctionDTO(
                rs.getLong("id"),
                seller.getUsername(),
                item.getItemName(),
                rs.getDouble("start_price"),
                rs.getDouble("step_price"),
                rs.getDouble("current_highest_bid"),
                highestBidderName,
                rs.getTimestamp("start_time").toInstant(),
                rs.getTimestamp("end_time").toInstant(),
                rs.getString("status"),
                rs.getInt("bid_count")
        );
        ItemDTO itemDTO = new ItemDTO(
                itemId,
                item.getItemName(),
                item.getTag(),
                sellerId,
                item.getImage(),
                item.getMieutaItem()
        );
        return AuctionDetailsDTO.join(auctionDTO, itemDTO);
    }

    public void updateEndTimeById(Long auctionId,Instant endTime){
        String sql = "UPDATE auctions SET end_time  = ? WHERE id = ?";
        try(Connection connection = getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setTimestamp(1,Timestamp.from(endTime));
            preparedStatement.setLong(2,auctionId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            log.error("Exception", e);
        }
    }
    public boolean updateAuctionById(Long auctionId, String newStatus) {
        String sql = "UPDATE auctions SET status = ? , updated_at = ? WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setTimestamp(2 , Timestamp.from(Instant.now()));
            ps.setLong(3, auctionId);
            // Thực thi lệnh UPDATE và trả về số dòng bị ảnh hưởng trong DB
            int rowsAffected = ps.executeUpdate();

            // Nếu số dòng bị tác động lớn hơn 0 tức là đã cập nhật thành công
            return rowsAffected > 0;

        } catch (SQLException e) {
            // Log lỗi chi tiết ra console của Server để dễ debug khi có sự cố
            log.error("Lỗi khi cập nhật trạng thái đấu giá (AuctionID: " + auctionId + "): " + e.getMessage());
            log.error("Exception", e);
            return false;
        }
    }
    public void closeAuctionbyId(long auctionId , long winnerId){
        if (updateAuctionById(auctionId, "CLOSED")){
            String sql = "UPDATE auctions SET winner_id = ? , updated_at = ? WHERE id = ?";
            try(Connection con = getConnection()){
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setLong(1, winnerId);
                ps.setTimestamp(2 , Timestamp.from(Instant.now()));
                ps.setLong(3, auctionId);
                int rows = ps.executeUpdate();
                if (rows > 0){
                    log.info("Đóng phiên thành công");
                }else {
                    log.info("Phiên đã đóng!");
                }
            }catch (SQLException e){
                log.error("Exception", e);
            }
        }
        else {
            throw new IllegalArgumentException("Có lỗi xảy ra khi đóng phiên!");
        }
    }
}