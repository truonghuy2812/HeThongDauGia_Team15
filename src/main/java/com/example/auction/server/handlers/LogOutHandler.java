package com.example.auction.server.handlers;

import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.server.service.ClientService;
import com.example.auction.shared.dto.MessageProtocol;

public class LogOutHandler extends RequestHandler{
    @Override
    public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws Exception {
        Long id = (Long) message.data();
//        ClientService.getInstance().removeClient(id);
        UserSession current = session.getCurrentUserSession();
        if (id == null && current != null) {
            id = current.getId();
        }
        session.setCurrentUserSession(null);
        if (id != null) {
            ClientService.getInstance().logoutMember(session, id);
        }
        sendResponse(session, new MessageProtocol(
          "LOGOUT",
          null,
          "SUCCESS",
          "Đăng xuất thành công"
        ));
    }
}
