package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;

public class AddProductController {

    @FXML private TextField itemName;
    @FXML private MenuButton menuButton;
    @FXML private TextArea infoText;
    @FXML private AnchorPane showInfo;
    @FXML private Button btnback;

    private File selectedImageFile; // Biến lưu trữ file ảnh

    @FXML
    public void initialize() {
        // TỐI ƯU UI: Tự động cập nhật chữ trên MenuButton khi người dùng chọn 1 danh mục
        if (menuButton != null) {
            for (MenuItem item : menuButton.getItems()) {
                item.setOnAction(event -> menuButton.setText(item.getText()));
            }
        }
    }

    @FXML
    private void addItem() {
        String productName = itemName.getText();
        String description = infoText.getText();
        String category = menuButton.getText();
        String seller = SessionManager.getCurrentUser(); // Lấy tên người đăng từ thẻ Session

        // 1. KIỂM TRA NHẬP LIỆU (FRONTEND)
        if (productName == null || productName.trim().isEmpty()) {
            SceneManager.getInstance().showAlert(AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập tên sản phẩm!");
            return;
        }

        if (selectedImageFile == null) {
            SceneManager.getInstance().showAlert(AlertType.WARNING, "Thiếu hình ảnh", "Vui lòng chọn hình ảnh cho sản phẩm!");
            return;
        }

        // TODO: [BACKEND] - Chuyển file ảnh (selectedImageFile) thành mảng byte[] hoặc chuỗi Base64.
        // TODO: [BACKEND] - Đóng gói (productName, category, description, seller, imageBytes) vào ItemDTO và bắn qua Socket.
        System.out.println("--- THÔNG TIN SẢN PHẨM MỚI ---");
        System.out.println("Người bán: " + seller);
        System.out.println("Tên SP: " + productName);
        System.out.println("Danh mục: " + category);
        System.out.println("Mô tả: " + description);
        System.out.println("File ảnh: " + selectedImageFile.getAbsolutePath());

        SceneManager.getInstance().showAlert(AlertType.INFORMATION, "Thành công", "Đăng bán sản phẩm thành công! (Giả lập)");

        // Đăng bán xong tự động quay về trang chủ
        back();
    }

    @FXML
    private void handlePickImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn hình ảnh cho sản phẩm");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        // TỐI ƯU: Lấy trực tiếp cửa sổ chính từ SceneManager thay vì ép kiểu từ menuButton
        selectedImageFile = fileChooser.showOpenDialog(SceneManager.getInstance().getPrimaryStage());

        // Xử lý hiển thị ảnh xem trước (Preview) sau khi chọn
        if (selectedImageFile != null) {
            System.out.println("Đã load ảnh thành công: " + selectedImageFile.getName());
            Image image = new Image(selectedImageFile.toURI().toString());
            ImageView imageView = new ImageView(image);

            // Căn chỉnh kích thước ảnh vừa với khung showInfo
            imageView.setFitWidth(showInfo.getPrefWidth() > 0 ? showInfo.getPrefWidth() : 350);
            imageView.setFitHeight(showInfo.getPrefHeight() > 0 ? showInfo.getPrefHeight() : 350);
            imageView.setPreserveRatio(true);

            showInfo.getChildren().clear();
            showInfo.getChildren().add(imageView);
        }
    }

    @FXML
    private void back() {
        System.out.println("Quay lại màn hình Trang chủ...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/MainAfterLogin.fxml", "Trang chủ hệ thống - Team 15");
    }
}