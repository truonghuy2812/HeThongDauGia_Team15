package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
public class LoginController {
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        // 1. Kiểm tra nhập liệu cơ bản
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
            return;
        }

        // TODO: Đoạn này sau sẽ được thay bằng code gửi DTO qua Socket để nhờ Server kiểm tra
        System.out.println("Đang gửi yêu cầu đăng nhập cho: " + username);
        // 2. Chuyển cảnh (Tạm thời test bằng tài khoản admin/123)
        if ("admin".equals(username) && "123".equals(password)) {
            System.out.println("Đăng nhập thành công!");
            goToMainScreen();
        } else {
            showAlert(Alert.AlertType.ERROR, "Thất bại", "Sai tên đăng nhập hoặc mật khẩu! (Tạm thời thử admin / 123)");
        }
    }
    @FXML
    private void goToRegister() {
        System.out.println("Chuyển sang màn hình Đăng ký...");
        try {
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            // Đảm bảo tên file FXML khớp với tên bạn vừa tạo
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/auction/client/view/signup.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);
            stage.setTitle("Đăng ký - Team 15");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải màn hình Đăng ký!");
        }
    }
    // Hàm tiện ích chuyển sang màn hình chính
    private void goToMainScreen() {
        try {
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            // Đảm bảo đường dẫn này khớp với thư mục dự án của bạn
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/auction/client/view/Main.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);
            stage.setTitle("Phần mềm Đấu giá - Team 15");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể tải màn hình chính!");
        }
    }

    // Hàm tiện ích hiển thị thông báo
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}