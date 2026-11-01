package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class AddProductController {

    @FXML private TextField itemName;
    @FXML private MenuButton menuButton;
    @FXML private TextArea infoText;
    @FXML private AnchorPane showInfo;

    private File selectedImageFile; //biến lưu trữ file ảnh

    //hàm thêm item
    @FXML
    private void addItem() {
        String productName = itemName.getText();
        String description = infoText.getText();
        String category = menuButton.getText();

        if (productName == null || productName.trim().isEmpty()) {
            showAlert(AlertType.ERROR, "Thiếu thông tin", "Vui lòng nhập tên sản phẩm!");
            return;
        }

        System.out.println("--- THÔNG TIN SẢN PHẨM MỚI ---");
        System.out.println("Tên SP: " + productName);
        System.out.println("Danh mục: " + category);
        System.out.println("Mô tả: " + description);

        showAlert(AlertType.INFORMATION, "Thành công", "Đọc dữ liệu FXML thành công! Kiểm tra Console.");
    }

    @FXML
    private void handlePickImage() {
        //mở cửa sổ File Explorer để chọn file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn hình ảnh cho sản phẩm");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        //lấy cửa sổ hiện tại để hiển thị popup
        Stage currentStage = (Stage) menuButton.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(currentStage);

        //xử lý ảnh sau khi nhặt
        if (selectedImageFile != null) {
            System.out.println("Đã load ảnh thành công: " + selectedImageFile.getName());

            Image image = new Image(selectedImageFile.toURI().toString());
            ImageView imageView = new ImageView(image);

            imageView.setFitWidth(400);
            imageView.setFitHeight(400);
            imageView.setPreserveRatio(true);

            showInfo.getChildren().clear();
            showInfo.getChildren().add(imageView);
        }
    }

    @FXML
    private void back() {
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