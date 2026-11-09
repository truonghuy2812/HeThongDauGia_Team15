package com.example.auction.server.handlers;

import com.example.auction.database.dao.NotificationDAO;
import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.NotificationDTO;

import java.util.List;

public class GetAllNotificationHandler extends RequestHandler {
    @Override
    public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws Exception {
        Long userId = (Long) message.data();
        List<NotificationDTO> list = NotificationDAO.getInstance().getAllNotification(userId);
        MessageProtocol response = new MessageProtocol("GET_ALL_NOTIFICATION",list,"SUCCESS","LAY THANH CONG");
        sendResponse(session,response);
    }
}
