package com.example.auction.server.entity;

import java.time.Instant;

public class FurnitureItem extends Item {
    private String material;      // Chất liệu: wood, metal, plastic, ...
    private String condition;     // Tình trạng: new, good, fair
    private String dimensions;    // Kích thước: "100x50x80" (cm)

    public FurnitureItem(String itemName, String mieutaItem, Long sellerId) {
        super(itemName, mieutaItem, sellerId, "FURNITURE");
    }

    // Constructor đầy đủ (khi tạo từ client)
    public FurnitureItem(String itemName, String mieutaItem, Long sellerId,
                         String material, String condition, String dimensions) {
        super(itemName, mieutaItem, sellerId, "FURNITURE");

        if (material == null || material.trim().isEmpty()) {
            throw new IllegalArgumentException("Material không được để trống");
        }
        if (condition == null || condition.trim().isEmpty()) {
            throw new IllegalArgumentException("Condition không được để trống");
        }
        if (dimensions == null || dimensions.trim().isEmpty()) {
            throw new IllegalArgumentException("Dimensions không được để trống");
        }

        this.material = material;
        this.condition = condition;
        this.dimensions = dimensions;
    }

    @Override
    public String getDetailed() {
        return "Furniture [Material: " + material + ", Condition: " + condition +
                ", Dimensions: " + dimensions + " cm]";
    }

    public String getMaterial() { return material; }
    public String getCondition() { return condition; }
    public String getDimensions() { return dimensions; }

    public void setMaterial(String material) {
        if (material == null || material.trim().isEmpty()) {
            throw new IllegalArgumentException("Material không được để trống");
        }
        this.material = material;
        this.setUpdatedAt(Instant.now());
    }

    public void setCondition(String condition) {
        if (condition == null || condition.trim().isEmpty()) {
            throw new IllegalArgumentException("Condition không được để trống");
        }
        this.condition = condition;
        this.setUpdatedAt(Instant.now());
    }

    public void setDimensions(String dimensions) {
        if (dimensions == null || dimensions.trim().isEmpty()) {
            throw new IllegalArgumentException("Dimensions không được để trống");
        }
        this.dimensions = dimensions;
        this.setUpdatedAt(Instant.now());
    }

    @Override
    public String toString() {
        return "FurnitureItem{" +
                "id=" + getId() +
                ", itemName='" + getItemName() + '\'' +
                ", material='" + material + '\'' +
                ", condition='" + condition + '\'' +
                ", dimensions='" + dimensions + '\'' +
                ", sellerId='" + getSellerId() + '\'' +
                '}';
    }
}