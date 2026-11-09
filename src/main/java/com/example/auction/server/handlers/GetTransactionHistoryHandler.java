package com.example.auction.server.handlers;

import com.example.auction.database.dao.PaymentRequestDAO;
import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.PaymentDTO;

import java.util.ArrayList;
import java.util.List;

public class GetTransactionHistoryHandler extends RequestHandler{
    @Override
    public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws Exception {
        if ("GET_TRANSACTION_HISTORY".equals(message.type())){
            Long userId = userSession.getId();
            List<PaymentDTO> paymentDTOList = new ArrayList<>();
            paymentDTOList = PaymentRequestDAO.getInstance().getAllRequest(userId);
            MessageProtocol messageProtocol = new MessageProtocol("GET_TRANSACTION_HISTORY",paymentDTOList,"SUCCESS","DA LAY THANH CONG");
            sendResponse(session,messageProtocol);
        }
    }
}
