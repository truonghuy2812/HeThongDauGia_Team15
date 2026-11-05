package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    // 1. ÁP DỤNG SINGLETON PATTERN
    private static SceneManager instance;
    private Stage primaryStage; // Biến lưu trữ cửa sổ gốc của Window

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
    //skibidi
    public void switchTo(Stage stage, String fxmlPath)
    {
        try {
            boolean wasFull = stage.isFullScreen();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newRoot = loader.load();
            Scene curScene = stage.getScene();

            if (curScene != null)
            {
                curScene.setRoot(newRoot);
            }
            else
            {
                stage.setScene(new Scene(newRoot));
            }

            if (wasFull) {
                stage.setFullScreen(true);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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
            showAlert(Alert.AlertType.ERROR, "Lỗi chuyển trang", "Không thể tải giao diện: " + fxmlPath);
        }
    }

    public void switchScene(String fxmlPath, String title) {
        switchScene(fxmlPath, title, 1000, 600);
    }
    // Nâng cấp: Cho phép truyền vào loại thông báo
    public void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Thêm 2 hằng số vẽ hình con mắt (Đóng / Mở) và hàm dùng chung này vào trong class SceneManager:
    private static final String EYE_SLASH_SVG = "M13.359 11.238C15.06 9.72 16 8 16 8s-3-5.5-8-5.5a7.028 7.028 0 0 0-2.79.588l.77.771A5.944 5.944 0 0 1 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.134 13.134 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755-.165.165-.337.328-.517.486l.708.709z M11.297 9.176a3.5 3.5 0 0 0-4.474-4.474l.823.823a2.5 2.5 0 0 1 2.829 2.829l.822.822zm-2.943 1.299.822.822a3.5 3.5 0 0 1-4.474-4.474l.823.823a2.5 2.5 0 0 0 2.829 2.829z M3.35 5.47c-.18.16-.353.322-.518.487A13.134 13.134 0 0 0 1.172 8l.195.288c.335.48.83 1.12 1.465 1.755C4.121 11.332 5.881 12.5 8 12.5c.716 0 1.39-.133 2.02-.36l.77.772A7.029 7.029 0 0 1 8 13.5C3 13.5 0 8 0 8s.939-1.721 2.641-3.238l.708.709zm10.296 8.884-12-12 .708-.708 12 12-.708.708z";
    private static final String EYE_OPEN_SVG = "M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z";
    public void setupPasswordToggle(javafx.scene.control.PasswordField passField, javafx.scene.shape.SVGPath eyeIcon) {
        if (passField == null || eyeIcon == null || !(passField.getParent() instanceof javafx.scene.layout.HBox hbox)) {
            return;
        }

        // Tạo một TextField chữ thường nằm cùng vị trí với PasswordField
        javafx.scene.control.TextField visibleField = new javafx.scene.control.TextField();
        visibleField.setPromptText(passField.getPromptText());
        visibleField.setStyle(passField.getStyle());
        visibleField.setFont(passField.getFont());
        visibleField.setPrefHeight(passField.getPrefHeight());
        javafx.scene.layout.HBox.setHgrow(visibleField, javafx.scene.layout.Priority.ALWAYS);

        visibleField.setVisible(false);
        visibleField.setManaged(false);

        // Đồng bộ nội dung 2 chiều: gõ ở ô nào thì txtPassword.getText() cũng tự nhận
        visibleField.textProperty().bindBidirectional(passField.textProperty());

        int index = hbox.getChildren().indexOf(passField);
        hbox.getChildren().add(index, visibleField);

        eyeIcon.setPickOnBounds(true);
        eyeIcon.setStyle("-fx-cursor: hand;");

        eyeIcon.setOnMouseClicked(event -> {
            boolean isShowing = visibleField.isVisible();
            if (isShowing) {
                visibleField.setVisible(false);
                visibleField.setManaged(false);
                passField.setVisible(true);
                passField.setManaged(true);
                eyeIcon.setContent(EYE_SLASH_SVG);
                passField.requestFocus();
                passField.positionCaret(passField.getText().length());
            } else {
                passField.setVisible(false);
                passField.setManaged(false);
                visibleField.setVisible(true);
                visibleField.setManaged(true);
                eyeIcon.setContent(EYE_OPEN_SVG);
                visibleField.requestFocus();
                visibleField.positionCaret(visibleField.getText().length());
            }
        });
    }
}