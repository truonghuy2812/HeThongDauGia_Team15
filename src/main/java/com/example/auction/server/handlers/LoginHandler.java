package com.example.auction.server.handlers;

import com.example.auction.database.dao.UserDAO;
import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.server.service.ClientService;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoginHandler extends RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public void handle(ClientSession session , MessageProtocol message, UserSession tempSession) throws IOException {
        log.info("1. SERVER ĐÃ VÀO LOGIN HANDLER - SẴN SÀNG XỬ LÝ");

        UserDTO userDTO = (UserDTO) message.data();
        log.info("2. DỮ LIỆU CLIENT GỬI LÊN: Email=" + userDTO.email() + " | Pass=" + userDTO.password());

        try {
            log.info("3. BẮT ĐẦU CHẠY VÀO DATABASE KIỂM TRA...");
            boolean isLoginValid = UserDAO.getInstance().checkUserLogin(userDTO);
            log.info("4. KẾT QUẢ TỪ DATABASE: " + isLoginValid);

            if (!isLoginValid) {
                // login thất bại
                sendResponse(session, new MessageProtocol("LOGIN", null, "ERROR", "Email hoặc password không đúng"));
                return;
            }

            log.info("5. ĐĂNG NHẬP HỢP LỆ, ĐANG TẠO USER SESSION...");
            UserSession userSession = UserDAO.getInstance().createUserSession(userDTO.email());

            if (userSession == null) {
                sendResponse(session, new MessageProtocol("LOGIN", null, "ERROR", "User không tìm thấy"));
                return;
            }

            // set user cho session
            session.setCurrentUserSession(userSession);

            log.info("6. User logged in: " + userSession.getUserName());

            UserDTO responseDTO = new UserDTO(
                    userSession.getId(),
                    userSession.getUserName(),
                    null,
                    userSession.getEmail(),
                    userSession.getRole().name(),
                    userSession.getPaymentAccount().convert()
            );

            ClientService.getInstance().signUpToMember(userSession.getId(), session);

            log.info("7. ĐANG GỬI PHẢN HỒI SUCCESS VỀ CHO CLIENT...");
            sendResponse(session, new MessageProtocol("LOGIN", responseDTO, "SUCCESS", "Đăng nhập thành công"));
            log.info("8. XONG! LUỒNG LOGIN KẾT THÚC TRỌN VẸN.");

        } catch (Exception e) {
            // QUAN TRỌNG: Phải in lỗi ra Console Server để còn biết đường sửa
            log.error("CÓ LỖI CRASH TRONG QUÁ TRÌNH ĐĂNG NHẬP!", e);
            sendResponse(session, new MessageProtocol("LOGIN", null, "ERROR", "Lỗi Server: " + e.getMessage()));
        }
    }
}