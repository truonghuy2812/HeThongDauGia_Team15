package com.example.auction.server.handlers;

import com.example.auction.database.dao.AutobidConfigDAO;
import com.example.auction.server.entity.AutobidConfig;
import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.server.service.ClientService;
import com.example.auction.shared.dto.AutobidDTO;
import com.example.auction.shared.dto.MessageProtocol;

import java.sql.SQLException;

public class GetAutobidConfigHandler extends RequestHandler{
  @Override
  public void handle(ClientSession session, MessageProtocol message, UserSession userSession) {
    AutobidDTO autobidDTO = (AutobidDTO) message.data();
    try{
      AutobidConfig autobidConfig = AutobidConfigDAO.getInstance().getConfigByAuctionAndUser(autobidDTO.auctionId, autobidDTO.userId);
      AutobidDTO data = autobidConfig.convert();
      MessageProtocol response = new MessageProtocol(
          "GETCONFIG",
          data,
          "SUCCESS",
          "THÀNH CÔNG"
      );
      ClientService.getInstance().sendTo(autobidDTO.userId, response);
    }catch (SQLException e){
      MessageProtocol response = new MessageProtocol(
          "GETCONFIG",
          null,
          "FAILED",
          "LỖI"
      );
      ClientService.getInstance().sendTo(autobidDTO.userId, response);
    }
  }
}
