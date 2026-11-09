package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;

public class AccountController {

    // =========================================================================
    // PHẦN 1: GIAO DIỆN (UI COMPONENTS)
    // =========================================================================
    @FXML private MenuButton userMenuButton;

    // Các trường hiển thị thông tin tài khoản
    @FXML private TextField txtProfileUsername;
    @FXML private TextField txtProfileEmail;
    @FXML private Label lblAccountUsername;

    // =========================================================================
    // PHẦN 2: KHỞI TẠO DỮ LIỆU
    // =========================================================================
    @FXML
    public void initialize() {
        System.out.println("Đã load màn hình Tài khoản của tôi.");

        if (SessionManager.isLoggedIn()) {
            String currentUser = SessionManager.getCurrentUser();

            // Cập nhật tên góc trên cùng bên phải
            if (userMenuButton != null) {
                userMenuButton.setText(currentUser);
            }

            // Điền thông tin vào form Hồ sơ
            if (txtProfileUsername != null) {
                txtProfileUsername.setText(currentUser);
            }
            if (txtProfileEmail != null) {
                txtProfileEmail.setText(currentUser + "@auction.com"); // Giả lập email
            }
            if (lblAccountUsername != null) {
                lblAccountUsername.setText(currentUser);
            }
        }

        // TODO: [BACKEND] Gọi API Server để lấy thông tin chi tiết thật của User (Email, SĐT, Địa chỉ...)
    }

    // =========================================================================
    // PHẦN 3: ĐIỀU HƯỚNG SIDEBAR
    // =========================================================================
    @FXML
    private void goToMain() {
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/MainAfterLogin.fxml", "Trang chủ hệ thống - Team 15");
    }

    @FXML
    private void goToMyBids() {
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/my-bids-view.fxml", "Đấu giá của tôi - Team 15");
    }

    @FXML
    private void goToMyProducts() {
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/my-product.fxml", "Sản phẩm của tôi - Team 15");
    }

    @FXML
    private void goToWallet() {
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/payment-view.fxml", "Ví và Nạp tiền - Team 15");
    }

    @FXML
    private void goToAddProduct() {
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/addproduct-view.fxml", "Đăng bán sản phẩm - Team 15");
    }

    @FXML
    private void LogoutAction() {
        SessionManager.logout();
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/Main.fxml", "Trang chủ hệ thống - Team 15");
    }
}