package com.example.auction.client.controller;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.SVGPath;
public class LoginController {
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        // 1. KIỂM TRA NHẬP LIỆU
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            // Đã sửa: Gọi hàm qua SceneManager
            SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
            return;
        }
        // TODO: [BACKEND] - Đóng gói username/password thành LoginDTO và gửi qua Socket tới Server.
        // TODO: [BACKEND] - Lắng nghe phản hồi từ Server xem mật khẩu có đúng không (true/false).
        System.out.println("Đang gửi yêu cầu đăng nhập cho: " + username);
        // 2. XỬ LÝ KẾT QUẢ ĐĂNG NHẬP (Tạm thời test bằng tài khoản admin/123)
        if ("guest".equals(username.trim()) && "123".equals(password.trim())) {
            System.out.println("Đăng nhập thành công!");
            SessionManager.login(username.trim());
            goToMainScreen();
        }
        else if ("admin".equals(username.trim()) && "123".equals(password.trim())) {
            System.out.println("Đăng nhập thành công!");
            SessionManager.login(username.trim());
            SceneManager.getInstance().switchScene("/com/example/auction/client/view/AdminController.fxml","Màn Duyệt đấu giá");
        }
        else {
            SceneManager.getInstance().showAlert(Alert.AlertType.ERROR, "Thất bại", "Sai tên đăng nhập hoặc mật khẩu! (Tạm thời thử admin hoặc guest / 123)");
        }
    }
    @FXML
    private void goToRegister() {
        System.out.println("Chuyển sang màn hình Đăng ký...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/signup.fxml", "Đăng ký tài khoản - Team 15");
    }
    private void goToMainScreen() {
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/MainAfterLogin.fxml", "Trang chủ hệ thống");
    }
    @FXML private SVGPath eyeIcon;
    @FXML
    public void initialize() {
        //Kích hoạt tính năng ẩn/hiện mật khẩu qua SceneManager
        SceneManager.getInstance().setupPasswordToggle(txtPassword, eyeIcon);
    }
}