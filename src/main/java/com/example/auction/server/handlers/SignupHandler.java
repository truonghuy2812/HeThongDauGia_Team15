package com.example.auction.server.handlers;

import com.example.auction.database.dao.PaymentAccountDAO;
import com.example.auction.database.dao.UserDAO;
import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SignupHandler extends RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(SignupHandler.class);

    @Override
    public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws IOException {

        UserDTO userDTO = (UserDTO) message.data();
        if ("ADMIN".equalsIgnoreCase(userDTO.role())) {
            log.info("CẢNH BÁO: Phát hiện hành vi hack quyền từ IP: " + session.getSocket().getRemoteSocketAddress());
            sendResponse(session, new MessageProtocol(
              "SIGNUP", null, "ERROR", "Hành vi đăng ký bất hợp pháp bị từ chối!"
            ));
            return; // Dừng xử lý luôn, không cho chạm vào DB
        }

        try {
            log.info(" Step 1: Checking if email exists...");
            boolean emailExists = UserDAO.getInstance().emailIsExit(userDTO.email());
            log.info("  Email exists? " + emailExists);
            if (UserDAO.getInstance().emailIsExit(userDTO.email())) {
                // Email đã tồn tại
                // GỌI sendResponse()
                sendResponse(session, new MessageProtocol(
                  "SIGNUP",
                  null,
                  "ERROR",
                  "Email đã tồn tại"
                ));
                return;
            }
            log.info("User registered: " + userDTO.username());

            Long userId = UserDAO.getInstance().addUserAndReturnId(userDTO);
            if (userId == null) {
                sendResponse(session, new MessageProtocol(
                  "SIGNUP",
                  null,
                  "ERROR",
                  "Không thể tạo tài khoản"
                ));
                return;
            }
            PaymentAccountDAO.getInstance().createPaymentAccountForOwner(userId);
            // RESPONSE: Send success
            // GỌI sendResponse()
            sendResponse(session, new MessageProtocol(
              "SIGNUP",
              userDTO,
              "SUCCESS",
              "Đăng ký thành công"
            ));

        } catch (Exception e) {
            // GỌI sendResponse()
            log.info(" EXCEPTION in SignupHandler:");
            log.info("  Exception class: " + e.getClass().getName());
            log.info("  Message: " + e.getMessage());
            log.info("  Stack trace:");
            log.error("Exception", e);
            sendResponse(session, new MessageProtocol(
              "SIGNUP",
              null,
              "ERROR",
              "Lỗi: " + e.getMessage()
            ));
        }
    }
}
