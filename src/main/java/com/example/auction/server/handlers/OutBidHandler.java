package com.example.auction.server.handlers;

import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.shared.dto.MessageProtocol;

public class OutBidHandler extends RequestHandler{
    @Override
    public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws Exception {
        // message.data se bao gom : ten cua nguoi vua bid
        // message.message() se la
    }
}
