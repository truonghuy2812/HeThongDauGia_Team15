package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class MainController {
    @FXML private TextField txtSearch;
    @FXML private FlowPane productContainer;

    @FXML
    public void initialize() {
        // TODO: [BACKEND] - Gọi Socket lấy danh sách Item từ Server và đổ vào productContainer
        System.out.println("Đã load giao diện trang chủ (Khách) thành công. Đang chờ data từ Server...");
    }
    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText();
        if (keyword.trim().isEmpty()) {
            SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập từ khóa tìm kiếm!");
            return;
        }
        // TODO: [BACKEND] - Gửi keyword lên Server để lọc sản phẩm
        System.out.println("Đang tìm kiếm sản phẩm với từ khóa: " + keyword);
        SceneManager.getInstance().showAlert(Alert.AlertType.INFORMATION, "Tìm kiếm", "Đang tìm: " + keyword + " (Giả lập)");
    }
    @FXML
    private void goToLogin() {
        System.out.println("Chuyển hướng sang màn hình Đăng Nhập...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/login-view.fxml", "Đăng nhập - Team 15");
    }
    @FXML
    private void goToSignup() {
        System.out.println("Chuyển hướng sang màn hình Đăng Ký...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/signup.fxml", "Đăng ký tài khoản - Team 15");
    }
    @FXML
    private void goToAddProduct() {
        if (!SessionManager.isLoggedIn()) {
            SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Yêu cầu đăng nhập", "Bạn phải đăng nhập thì mới được đăng bán sản phẩm!");
            goToLogin();
            return;
        }
        System.out.println("User " + SessionManager.getCurrentUser() + " đang mở màn hình Thêm Sản Phẩm...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/addproduct-view.fxml", "Thêm Sản Phẩm Mới - Team 15");
    }
}