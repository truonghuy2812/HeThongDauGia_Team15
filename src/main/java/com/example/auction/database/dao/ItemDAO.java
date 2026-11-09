package com.example.auction.database.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.auction.server.entity.Item;
import com.example.auction.server.entity.ItemFactory;
import com.example.auction.shared.dto.ItemDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO extends BaseDAO {
    private static final Logger log = LoggerFactory.getLogger(ItemDAO.class);

    private static ItemDAO instance;
    private ItemDAO() {}
    public static ItemDAO getInstance() {
        if (instance == null) {
            synchronized (ItemDAO.class) {
                instance = new ItemDAO();
            }
        }
        return instance;
    }
    public Item getItembyId(Long id){
        String sql = "SELECT * FROM item_table WHERE item_id = ?";
        Item item = null;
        try (Connection con = getConnection()){
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1 , id);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    item = ItemFactory.getItem(rs.getString("tag"));
                    item.setId(rs.getLong("item_id"));
                    item.setItemName(rs.getString("item_name"));
                    item.setImage(rs.getString("item_image"));
                    item.setSellerId(rs.getLong("seller_id"));
                }
            }
        }catch (SQLException e){
            log.error("Exception", e);
        }
        return item;
    }
    public List<ItemDTO> getAllItemsBySellerId(Long sellerId){
        String sql = "SELECT * FROM item_table WHERE seller_id = ?";
        List<ItemDTO> res = new ArrayList<>();
        try(Connection connection = getConnection()){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1 , sellerId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
//        Long id,
//        String itemName,
//        String tag,
//        Long sellerId,
//        String imageUrl,
//        String info
                res.add(new ItemDTO(
                        rs.getLong("item_id"),
                        rs.getString("item_name"),
                        rs.getString("tag"),
                        rs.getLong("seller_id"),
                        rs.getString("item_image"),
                        rs.getString("info")
                ));
            }
            return res;
        }catch (SQLException e){
            log.error("Exception", e);
        }
        return null;
    }
    public void addItem(ItemDTO itemDTO){
        String sql = "INSERT INTO item_table (item_name, item_image, tag, seller_id, info) VALUES (?,?,?,?,?)";
        try(Connection connection = getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, itemDTO.itemName());
            preparedStatement.setString(2, itemDTO.imageUrl());
            preparedStatement.setString(3, itemDTO.tag());
            preparedStatement.setLong(4, itemDTO.sellerId());
            preparedStatement.setString(5, itemDTO.info());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
