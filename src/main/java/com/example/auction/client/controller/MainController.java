package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class MainController {

    @FXML private TextField txtSearch;
    @FXML private FlowPane productContainer;

    // Các nút đề mục lớn bên cột Sidebar trái
    @FXML private Button btnNavHome;
    @FXML private Button btnNavMyBids;
    @FXML private Button btnNavMyProducts;
    @FXML private Button btnNavWallet;
    @FXML private Button btnNavAccount;

    private static final String ACTIVE_BTN_STYLE =
            "-fx-background-color: #fce8e9; -fx-background-radius: 5; -fx-text-fill: #d31126; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String INACTIVE_BTN_STYLE =
            "-fx-background-color: transparent; -fx-background-radius: 5; -fx-text-fill: #4b5563; -fx-font-weight: normal; -fx-cursor: hand;";

    @FXML
    public void initialize() {
        // Mặc định luôn sáng nút Trang chủ cho Khách
        setActiveSidebarButton(btnNavHome);

        // TODO: [BACKEND] - Gọi Socket lấy danh sách Item từ Server và đổ vào productContainer
        System.out.println("Đã load giao diện trang chủ (Khách) thành công. Đang chờ data từ Server...");
    }

    // Hàm đổi màu nút (cho Khách thì chỉ sáng nút Home, các nút khác bấm vào sẽ báo lỗi)
    private void setActiveSidebarButton(Button activeButton) {
        Button[] navButtons = {btnNavHome, btnNavMyBids, btnNavMyProducts, btnNavWallet, btnNavAccount};
        for (Button btn : navButtons) {
            if (btn != null) {
                btn.setStyle(btn == activeButton ? ACTIVE_BTN_STYLE : INACTIVE_BTN_STYLE);
            }
        }
    }

    // 1. Chuyển về mục Trang chủ
    @FXML
    private void showHomeView() {
        setActiveSidebarButton(btnNavHome);
    }

    // CÁC MỤC YÊU CẦU ĐĂNG NHẬP
    @FXML
    private void handleRequiresLogin() {
        // Vẫn giữ sáng nút Trang chủ
        setActiveSidebarButton(btnNavHome);
        SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Yêu cầu đăng nhập", "Bạn cần đăng nhập để truy cập tính năng này!");
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