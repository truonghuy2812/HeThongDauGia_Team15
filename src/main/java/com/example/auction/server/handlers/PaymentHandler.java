package com.example.auction.server.handlers;

import com.example.auction.database.dao.PaymentAccountDAO;
import com.example.auction.database.dao.PaymentRequestDAO;
import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.server.service.ClientService;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.PaymentAccountDTO;
import com.example.auction.shared.dto.PaymentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PaymentHandler extends RequestHandler{
    private static final Logger log = LoggerFactory.getLogger(PaymentHandler.class);

    @Override
    public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws Exception {
        if ("NEW_DEPOSIT_PENDING".equals(message.type())){
            PaymentDTO paymentDTO = (PaymentDTO) message.data();
            Long id = PaymentRequestDAO.getInstance().createPaymentRequest(paymentDTO);
            PaymentDTO sendAdmin = new PaymentDTO(paymentDTO.ownerId(), paymentDTO.amount(), paymentDTO.status(), paymentDTO.time(),id, paymentDTO.message());
            MessageProtocol messageProtocol = new MessageProtocol("NEW_DEPOSIT_PENDING",sendAdmin,"PENDING","NAP THANH CONG");
            // gui message cho admin
//            ClientService.getInstance().sendTo(1L,messageProtocol);
            ClientService.getInstance().sendToAllAdmins(messageProtocol);
        }
        if ("WITHDRAW".equals(message.type())){
            PaymentDTO paymentDTO = (PaymentDTO) message.data();
            PaymentAccountDTO before = PaymentAccountDAO.getInstance().getAccountByOwnerId(paymentDTO.ownerId());
            PaymentAccountDAO.getInstance().updateAccountByOwnerId(paymentDTO.ownerId(), before.balance()-paymentDTO.amount(), paymentDTO.status());
            PaymentAccountDTO paymentAccount = PaymentAccountDAO.getInstance().getAccountByOwnerId(paymentDTO.ownerId());
            MessageProtocol messageProtocol = new MessageProtocol("WITHDRAW",paymentAccount,"SUCCESS","NAP THANH CONG");
            sendResponse(session,messageProtocol);
        }
        if ("APPROVE_DEPOSIT".equals(message.type())){
            PaymentDTO paymentDTO = (PaymentDTO) message.data();
            PaymentAccountDTO before = PaymentAccountDAO.getInstance().getAccountByOwnerId(paymentDTO.ownerId());
            PaymentAccountDAO.getInstance().updateAccountByOwnerId(paymentDTO.ownerId(), before.balance()+paymentDTO.amount(), paymentDTO.status());
            PaymentAccountDTO paymentAccount = PaymentAccountDAO.getInstance().getAccountByOwnerId(paymentDTO.ownerId());
            MessageProtocol messageProtocol = new MessageProtocol("APPROVE_DEPOSIT",paymentAccount,"SUCCESS","NAP THANH CONG");
            // gui message den nguoi dung

            PaymentRequestDAO.getInstance().approvePaymentRequest( paymentDTO.id());
            ClientService.getInstance().sendTo(paymentDTO.ownerId(),messageProtocol);
        }
        if ("GET_DEPOSIT_PENDING".equals(message.type())){
            List<PaymentDTO> list = PaymentRequestDAO.getInstance().getAllPendingPaymentRequest();
            for (PaymentDTO payment : list){
                log.debug("{}", payment.toString());
            }
            MessageProtocol messageProtocol = new MessageProtocol("GET_DEPOSIT_PENDING",list,"SUCCESS","LAY THANH CONG");
//            ClientService.getInstance().sendTo(1L,messageProtocol);
            ClientService.getInstance().sendToAllAdmins(messageProtocol);
        }

    }
}
