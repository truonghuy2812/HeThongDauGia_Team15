package com.example.auction.server.handlers;

import com.example.auction.database.dao.ItemDAO;
import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.shared.dto.ItemDTO;
import com.example.auction.shared.dto.MessageProtocol;

import java.util.List;

public class GetAddedProductsHandler extends RequestHandler{
  @Override
  public void handle(ClientSession session , MessageProtocol message, UserSession userSession){
   List<ItemDTO> list =  ItemDAO.getInstance().getAllItemsBySellerId(userSession.getId());
   MessageProtocol response = new MessageProtocol(
       "GET_ADDED_PRODUCT",
       list,
       "SUCCESS",
       "Đây là các sản phẩm đã thêm");
    sendResponse(session, response);
  }
}
