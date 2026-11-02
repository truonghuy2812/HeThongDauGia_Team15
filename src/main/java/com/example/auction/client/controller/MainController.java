package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {

    @FXML
    private Button btnGoToAddProduct; // Gắn id này cho nút trên Scene Builder

    @FXML
    private void goToAddProduct() {
        Stage stage = (Stage) btnGoToAddProduct.getScene().getWindow();
        try {
            // Đường dẫn tuyệt đối trỏ tới file FXML thêm sản phẩm
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/auction/client/view/addproduct-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);
            stage.setTitle("Thêm Sản Phẩm Mới - Team 15");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            System.out.println("Lỗi load file addproduct-view.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}