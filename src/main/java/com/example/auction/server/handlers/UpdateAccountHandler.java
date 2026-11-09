package com.example.auction.server.handlers;

import com.example.auction.database.dao.UserDAO;
import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateAccountHandler extends RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(UpdateAccountHandler.class);

    @Override
    public void handle(ClientSession session, MessageProtocol message, UserSession temp) throws Exception {
        String emailUser = (String) message.data();
        try {
            UserSession userSession = UserDAO.getInstance().createUserSession(emailUser);
            session.setCurrentUserSession(userSession);
            UserDTO responseDTO = new UserDTO(
                    userSession.getId(),
                    userSession.getUserName(),
                    null,
                    userSession.getEmail(),
                    userSession.getRole().name(),
                    userSession.getPaymentAccount().convert()
            );
            MessageProtocol messageProtocol = new MessageProtocol("UPDATE_ACCOUNT",responseDTO,"SUCCESS","Cập nhật thành công");
            sendResponse(session,messageProtocol);
        }catch (Exception e){
            log.error("Exception", e);
        }
    }
}
