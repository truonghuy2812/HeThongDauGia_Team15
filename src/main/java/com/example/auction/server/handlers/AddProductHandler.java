package com.example.auction.server.handlers;

import com.example.auction.database.dao.ItemDAO;
import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.shared.dto.ItemDTO;
import com.example.auction.shared.dto.MessageProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// noi server xu ly request
public class AddProductHandler extends RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(AddProductHandler.class);


    @Override
    public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws Exception {
        ItemDTO thongTinSanPham = (ItemDTO) message.data();
        ItemDAO.getInstance().addItem(thongTinSanPham);

        log.info("Thêm sản phẩm thành công!");
    }
}
