package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignupController {

    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;

    @FXML
    private void handleRegister() {
        String username = txtUsername.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        // 1. KIỂM TRA DỮ LIỆU ĐẦU VÀO (FRONTEND)
        if (username.trim().isEmpty() || email.trim().isEmpty() ||
                password.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            SceneManager.getInstance().showAlert(Alert.AlertType.ERROR, "Lỗi mật khẩu", "Mật khẩu nhập lại không khớp!");
            return;
        }

        // TODO: [BACKEND] - Đóng gói thông tin thành RegisterDTO/UserDTO và gửi qua Socket về Server.
        // TODO: [BACKEND] - Chờ phản hồi từ Server (kiểm tra trùng username/email) trước khi báo thành công.
        System.out.println("Đang gửi yêu cầu đăng ký cho: " + username + " - Email: " + email);

        SceneManager.getInstance().showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đăng ký thành công! (Giả lập)");

        // Đăng ký xong tự động điều hướng về form đăng nhập
        goToLogin();
    }

    @FXML
    private void goToLogin() {
        System.out.println("Chuyển hướng về màn hình Đăng nhập...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/login-view.fxml", "Đăng nhập - Team 15");
    }
    @FXML private javafx.scene.shape.SVGPath eyeIconPass;
    @FXML private javafx.scene.shape.SVGPath eyeIconConfirm;
    @FXML
    public void initialize() {
        SceneManager.getInstance().setupPasswordToggle(txtPassword, eyeIconPass);
        SceneManager.getInstance().setupPasswordToggle(txtConfirmPassword, eyeIconConfirm);
    }
}