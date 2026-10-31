package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AddProductController {

    // Ánh xạ chính xác các fx:id từ FXML của Team 10
    @FXML
    private TextField itemName;

    @FXML
    private MenuButton menuButton;

    @FXML
    private TextArea infoText;

    // TODO: Giao diện đang thiếu TextField cho giá tiền, cần bổ sung sau

    @FXML
    private void addItem() {
        // Lấy dữ liệu từ giao diện
        String productName = itemName.getText();
        String description = infoText.getText();
        String category = menuButton.getText();

        // Validate cơ bản
        if (productName == null || productName.trim().isEmpty()) {
            showAlert(AlertType.ERROR, "Thiếu thông tin", "Vui lòng nhập tên sản phẩm!");
            return;
        }

        // In ra console để test
        System.out.println("--- THÔNG TIN SẢN PHẨM MỚI ---");
        System.out.println("Tên SP: " + productName);
        System.out.println("Danh mục: " + category);
        System.out.println("Mô tả: " + description);

        showAlert(AlertType.INFORMATION, "Thành công", "Đọc dữ liệu FXML thành công! Kiểm tra Console.");
    }

    @FXML
    private void handlePickImage() {
        // Hàm này chạy khi bấm nút "Chọn tệp ảnh"
        System.out.println("Đang mở cửa sổ chọn ảnh...");
    }

    @FXML
    private void back() {
        // Hàm này chạy khi bấm nút "Quay lại"
        System.out.println("Quay lại màn hình trước...");
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}