package com.example.auction.database.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.auction.server.entity.PaymentAccount;
import com.example.auction.server.entity.User;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.shared.dto.PaymentAccountDTO;
import com.example.auction.shared.dto.UserDTO;
import com.example.auction.server.entity.Role;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// UserDAO truy cap bang lien quan den user

// Chi danh cho cac thong tin lien quan den user
public class UserDAO extends BaseDAO {
    private static final Logger log = LoggerFactory.getLogger(UserDAO.class);

    private static UserDAO instance;

    private UserDAO() {
    }

    public static UserDAO getInstance() {
        if (instance == null) {
            synchronized (UserDAO.class) {
                if (instance == null) instance = new UserDAO();
            }
        }
        return instance;
    }

    // ktra email đã tồn tại trong database kh
    public boolean emailIsExit(String email) {
        String sql = "SELECT 1 FROM usertable where email = ? LIMIT 1 ";
        try (Connection connection = getConnection();  // lấy kết nối
             PreparedStatement prepareStatement = connection.prepareStatement(sql)) {

            // Set tham số: email = ?
            prepareStatement.setString(1, email);

            // thực thi query
            try (ResultSet rs = prepareStatement.executeQuery()) {
                return rs.next();  // Có row trả về? → true, không có → false
            }
        } catch (SQLException e) {
            log.error("Exception", e);
        }
        return false;
    }

    // them user vao bang (vao database), tra ve id neu thanh cong
    public Long addUserAndReturnId(UserDTO userDTO) {
        if (emailIsExit(userDTO.email())) {
            log.info("Email da ton tai");
            return null;
        }
        String sql = "INSERT INTO usertable (email, username, password, role) VALUES (?,?,?,?)";
//        if (!emailIsExit(userDTO.email())) {
//            try (Connection connection = getConnection()) {
//                PreparedStatement preparedStatement = connection.prepareStatement(sql);
//                String hashedPassword = BCrypt.hashpw(userDTO.password(), BCrypt.gensalt());
//                preparedStatement.setString(1, userDTO.email());
//                preparedStatement.setString(2, userDTO.username());
//                preparedStatement.setString(3, hashedPassword);
//                preparedStatement.setString(4, userDTO.role());
//                preparedStatement.executeUpdate();
//            } catch (SQLException e) {
//                log.error("Exception", e);
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            String hashedPassword = BCrypt.hashpw(userDTO.password(), BCrypt.gensalt());
            preparedStatement.setString(1, userDTO.email());
            preparedStatement.setString(2, userDTO.username());
            preparedStatement.setString(3, hashedPassword);
            preparedStatement.setString(4, userDTO.role());
            int rows = preparedStatement.executeUpdate();
            if (rows == 0) {
                return null;
            }
            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        } catch (SQLException e) {
            log.error("Exception", e);
        }
        return null;
    }

    /** @deprecated dùng {@link #addUserAndReturnId(UserDTO)} */
    public void addUser(UserDTO userDTO) {
        addUserAndReturnId(userDTO);
    }

    /** Lấy id tất cả tài khoản ADMIN (để gửi thông báo real-time). */
    public List<Long> getAdminUserIds() {
        String sql = "SELECT id FROM usertable WHERE role = ?";
        List<Long> adminIds = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, Role.ADMIN.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    adminIds.add(rs.getLong("id"));
                }
            }
        } catch (SQLException e) {
            log.error("Exception", e);
        }
        return adminIds;
    }

    // Kiểm tra login (email + password có hợp lệ không)
    public boolean checkUserLogin(UserDTO userDTO) {
        String email = userDTO.email();
        String pass = userDTO.password();
        String sql = "SELECT password FROM usertable where email = ? LIMIT 1 ";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // Set email parameter
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Có user với email này
                    String dbPassword = resultSet.getString("password");
                    boolean isMatched = BCrypt.checkpw(pass, dbPassword);
                    return isMatched;
                }
                else {
                    log.info("Không tìm thấy bất kỳ dòng nào có Email: [" + email + "] trong DB!");
                }
            }
        } catch (SQLException e) {
            log.error("Exception", e);
        }
        log.info("Tai khoan khong ton tai");
        return false;
    }
    public UserSession createUserSession(String email) {
        String sql = "SELECT id, email, username, role FROM usertable WHERE email = ? LIMIT 1";
        String sql1 = "SELECT * FROM payment_accounts WHERE owner_id = ? LIMIT 1";
        UserSession userSession = null;
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    log.info("{}", email);
                    long ownerId = resultSet.getLong("id");
                    log.info("OWNER ID: " + ownerId);
                    PreparedStatement ps = connection.prepareStatement(sql1);
                    ps.setLong(1, ownerId);
                    PaymentAccountDTO paymentAccountDTO = null;
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            log.info("TIM THAY ACCOUNT");
                            paymentAccountDTO = new PaymentAccountDTO(
                                    ownerId,
                                    rs.getDouble("balance"),
                                    rs.getDouble("frozen_balance"),
                                    Instant.now());
                        }
                    }
                    log.info("PAYMENT: " + (paymentAccountDTO == null));
                    String roleString = resultSet.getString("role");
                    Role role = Role.valueOf(roleString);  // Convert String → Role enum
                    userSession = new UserSession(
                            ownerId,
                            resultSet.getString("username"),
                            resultSet.getString("email"),
                            role,
                            new PaymentAccount(paymentAccountDTO)
                    );
                }
            }
        } catch (SQLException e) {
            log.error("Exception", e);
            return null;
        }
        return userSession;
    }

    public User getUserbyId(Long id) {
        String sql = "SELECT * FROM usertable WHERE id = ?";
        User user = new User();
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            Role.valueOf(rs.getString("role")));
                    PaymentAccountDTO paymentAccountDTO = PaymentAccountDAO.getInstance().getAccountByOwnerId(id);
                    user.setPaymentAccount(new PaymentAccount(paymentAccountDTO));
                }
            }
        } catch (SQLException e) {
            log.error("Exception", e);
        }
        return user;
    }


    public String getUsernameByBidderId(Long bidderId) {
        String sql = "SELECT u.username FROM usertable u " +
                "JOIN bids b ON u.id = b.bidder_id " +
                "WHERE b.bidder_id = ? LIMIT 1";
        String username;
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, bidderId);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            username = resultSet.getString("username");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return username;
    }
}
