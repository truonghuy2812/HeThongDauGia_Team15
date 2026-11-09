package com.example.auction.server.entity;
//Abstract base class cho tất cả loại sản phẩm được đấu giá
// kế thừa từ Entity id, createAt, updateAt
// các subclass là - ArtItem (tranh vẽ, tượng,...) , ElectronicsItem (laptop, điện thoại,...)

public abstract class Item extends Entity {
    private String itemName;
    private String mieutaItem;
    private String tag;
    private Long sellerId;
    private String image;

    public String getImage() {
        return image;
    }

    public Item(String itemName, String mieutaItem, Long sellerId, String tag) {
        super(); // Gọi Entity constructor để set createdAt, updatedAt
        this.tag = tag;
        this.itemName = itemName;
        this.mieutaItem = mieutaItem;
        this.sellerId = sellerId;
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name không được để trống");
        }
        if (sellerId == null) {
            throw new IllegalArgumentException("Seller id không được để trống");
        }
    }

    public abstract String getDetailed(); // mỗi loại sp sẽ tự định nghĩa cách hiển thị thông tin chi tiết của nó

    public String getItemName() { return itemName; }
    public String getMieutaItem() { return mieutaItem; }
    public Long getSellerId() { return sellerId; }
    public String getTag(){
        return tag;
    }
    public void setItemName(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name không được để trống");
        }
        this.itemName = itemName.trim();
        this.setUpdatedAt(java.time.Instant.now());
    }
    public void setMieutaItem(String mieutaItem) {
        if (mieutaItem == null || mieutaItem.trim().isEmpty()) {
            throw new IllegalArgumentException("Mô tả item không được để trống");
        }
        this.mieutaItem= mieutaItem.trim();
        this.setUpdatedAt(java.time.Instant.now());
    }


    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + getId() +
                ", itemName='" + itemName + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", createdAt=" + getCreatedAt() +
                '}';
    }

}
