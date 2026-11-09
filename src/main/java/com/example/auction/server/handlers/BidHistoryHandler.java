package com.example.auction.server.handlers;

import com.example.auction.database.dao.BidDAO;
import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.shared.dto.BidDTO;
import com.example.auction.shared.dto.MessageProtocol;

import java.util.List;

public class BidHistoryHandler extends RequestHandler{
    @Override
    public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws Exception {
        if ("BID_HISTORY".equals(message.type())){
            Long id = (Long) message.data();
            List<BidDTO> bidDTOList = BidDAO.getInstance().getAllBid(id);
            MessageProtocol messageProtocol = new MessageProtocol("BID_HISTORY",bidDTOList,"SUCCESS","CAP NHAT BIDHISTORY");
            sendResponse(session,messageProtocol);
        }
    }
}
