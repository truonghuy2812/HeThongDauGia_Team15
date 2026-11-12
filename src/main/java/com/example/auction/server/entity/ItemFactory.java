package com.example.auction.server.entity;

import java.util.HashMap;
import java.util.Map;

public abstract class ItemFactory {
    private static final Map<String , Item> items = new HashMap<>();
    static {
        items.put("ELECTRONIC" , new ElectronicsItem("N/A" , "N/A", 1L));
        items.put("ART" , new ArtItem("N/A" , "N/A", 1L));
        items.put("FURNITURE", new FurnitureItem("N/A", "N/A", 1L));
    }
    public static Item getItem(String tag) throws IllegalArgumentException{
        Item res = items.get(tag);
        if (res == null){
            throw new IllegalArgumentException("Chưa có loại này!");
        }
        return res;
    }
}
