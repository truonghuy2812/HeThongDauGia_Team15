package com.example.auction.shared.dto;

import java.io.Serializable;

/**
 * ItemDTO - Chỉ chứa các thông tin cần thiết để hiện thị Card sản phẩm ở màn hình chính
 * Giúp giảm tải băng thông network so với AuctionDTO đầy đủ.
 */
public record ItemDTO(
  Long id,
  String itemName,
  String tag,
  Long sellerId,
  String imageUrl, // Sau này thêm ảnh sẽ dùng
  String info
) implements Serializable {
  private static final long serialVersionUID = 1L;

  public ItemDTO(String itemName, String tag, Long sellerId, String imageUrl, String info) {
    this(0L, itemName, tag, sellerId, imageUrl, info);
  }
}
