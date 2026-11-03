package com.example.auction.client.controller;
import javafx.stage.Stage;import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import java.io.IOException;
public class SceneManager {
    // 1. ÁP DỤNG SINGLETON PATTERN
    private static SceneManager instance;
    private Stage primaryStage; //Biến lưu trữ của sổ gốc của Window
    private SceneManager() {}
    public static synchronized SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }
    // 2. QUẢN LÝ KHUNG CỬA SỔ (STAGE)
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    // 3. HÀM CHUYỂN CẢNH DÙNG CHUNG
    public void switchScene(String fxmlPath, String title, double width, double height) {
        if (primaryStage == null) {
            System.err.println("❌ LỖI: Chưa khởi tạo PrimaryStage trong SceneManager!");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), width, height);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi chuyển trang", "Không thể tải giao diện: " + fxmlPath);
        }
    }
    public void switchScene(String fxmlPath, String title) {
        switchScene(fxmlPath, title, 1000, 600);
    }
    public void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
