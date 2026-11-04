package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AutoBidFormController {

    @FXML private Label lblProductName;
    @FXML private Label lblCurrentPrice;

    // Ô nhập "Giá tối đa" mà người dùng chấp nhận trả
    @FXML private TextField txtMaxPrice;

    // Biến lưu trữ ID của sản phẩm đang được thiết lập Auto Bid
    private int currentProductId;

    @FXML
    public void initialize() {
        System.out.println("Đã load form thiết lập Đặt giá tự động (Auto Bid).");
        // TODO: [BACKEND] - Nhận thông tin sản phẩm từ DetailController truyền sang để hiển thị.
        lblProductName.setText("Sản phẩm đang chọn (Giả lập)");
    }

    /**
     * Xử lý lưu cấu hình Đặt giá tự động
     */
    @FXML
    private void handleSaveAutoBid() {
        String maxPriceStr = txtMaxPrice.getText();

        if (maxPriceStr == null || maxPriceStr.trim().isEmpty()) {
            SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Nhập thiếu", "Vui lòng nhập mức giá tối đa bạn sẵn sàng trả.");
            return;
        }

        try {
            double maxPrice = Double.parseDouble(maxPriceStr);
            String currentUser = SessionManager.getCurrentUser();

            // TODO: [BACKEND] - Đóng gói thông tin (currentUser, currentProductId, maxPrice)
            // thành AutoBidDTO và gửi qua Socket lên Server để đăng ký luồng chạy ngầm.

            System.out.println("User: " + currentUser + " đã kích hoạt AutoBid cho SP ID: " + currentProductId + " với giá tối đa: " + maxPrice);
            SceneManager.getInstance().showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thiết lập Auto Bid! Hệ thống sẽ tự động đấu giá thay bạn.");

            // Cài đặt xong thì đóng form, quay về trang Chi tiết sản phẩm
            backToDetail();

        } catch (NumberFormatException e) {
            SceneManager.getInstance().showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Vui lòng nhập số tiền hợp lệ (không chứa chữ cái).");
        }
    }

    /**
     * Hủy thiết lập (Nếu người dùng muốn tắt Auto Bid cũ)
     */
    @FXML
    private void handleCancelAutoBid() {
        // TODO: [BACKEND] - Gửi yêu cầu hủy Auto Bid cho sản phẩm này lên Server.
        System.out.println("Đã hủy Auto Bid cho sản phẩm.");
        SceneManager.getInstance().showAlert(Alert.AlertType.INFORMATION, "Đã hủy", "Đã tắt tính năng đấu giá tự động cho sản phẩm này.");
        txtMaxPrice.clear();
    }

    /**
     * Nút Quay lại màn hình Chi tiết sản phẩm
     */
    @FXML
    private void backToDetail() {
        System.out.println("Quay lại màn hình Chi tiết sản phẩm...");
        // Trở về giao diện Detail (Tùy thuộc vào đường dẫn FXML chi tiết của bạn)
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/detail-view.fxml", "Chi tiết Sản phẩm - Team 15");
    }
}