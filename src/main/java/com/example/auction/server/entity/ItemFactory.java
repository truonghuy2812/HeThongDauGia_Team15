package com.example.auction.server.entity;

public class ItemFactory {

    // Hàm phải là static và trả về một đối tượng kiểu Item
    public static Item getItem(String tag) {
        if (tag == null) {
            return new Item(); // Trả về Item mặc định nếu tag rỗng
        }

        // Tùy vào dự án của nhóm ông có chia loại sản phẩm không (Factory Pattern)
        switch (tag.toUpperCase()) {
            case "ELECTRONIC":
                // Nếu nhóm có class ElectronicItem kế thừa Item
                return new ElectronicsItem();
            case "ART":
                // Nếu nhóm có class ArtItem kế thừa Item
                return new ArtItem();
            default:
                // Nếu không chia loại hoặc tag không khớp, trả về Item gốc
                return new Item();
        }
    }
}