package com.example.auction.server.entity;

import java.io.Serializable;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String itemName;
    private String tag;
    private String image;
    private Long  sellerId;
    public Item() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}
