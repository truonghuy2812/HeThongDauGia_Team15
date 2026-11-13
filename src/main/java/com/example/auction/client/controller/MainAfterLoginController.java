package com.example.auction.client.controller;

import com.example.auction.client.view.ClockUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class MainAfterLoginController {

    @FXML private TextField txtSearch;
    @FXML private FlowPane productContainer;
    @FXML private MenuButton userMenuButton;
    @FXML private Label lblClock;
    @FXML
    public void initialize() {
        ClockUtil.bindClockToLabel(lblClock);
        if (SessionManager.isLoggedIn() && userMenuButton != null) {
            userMenuButton.setText(SessionManager.getCurrentUser());
        }

        // TODO: [BACKEND] - Gọi Socket gửi lệnh yêu cầu Server trả về danh sách Item.
        System.out.println("Đã load giao diện trang chủ thành công. Đang chờ data từ Server...");
    }

    // =========================================================================
    // CÁC HÀM ĐIỀU HƯỚNG SIDEBAR
    // =========================================================================

    // (Lưu ý: Không cần hàm showHomeView vì đây chính là Home)

    @FXML
    private void showMyBidsView() {
        System.out.println("Chuyển sang màn hình Đấu giá của tôi...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/my-bids-view.fxml", "Đấu giá của tôi - Team 15");
    }

    @FXML
    private void showMyProductsView() {
        System.out.println("Chuyển sang màn hình Sản phẩm của tôi...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/my-product.fxml", "Sản phẩm của tôi - Team 15");
    }

    @FXML
    private void showWalletView() {
        System.out.println("Chuyển sang màn hình Ví & Nạp tiền...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/payment-view.fxml", "Ví và Nạp tiền - Team 15");
    }

    @FXML
    private void goToAccount() {
        System.out.println("Chuyển sang màn hình Tài khoản của tôi...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/account-view.fxml", "Tài khoản của tôi - Team 15");
    }

    @FXML
    private void goToAddProduct() {
        System.out.println("User " + SessionManager.getCurrentUser() + " đang mở màn hình Thêm Sản Phẩm...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/addproduct-view.fxml", "Thêm Sản Phẩm Mới - Team 15");
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText();
        if (keyword.trim().isEmpty()) {
            SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập từ khóa tìm kiếm!");
            return;
        }
        System.out.println("Đang tìm kiếm sản phẩm với từ khóa: " + keyword);
        SceneManager.getInstance().showAlert(Alert.AlertType.INFORMATION, "Tìm kiếm", "Đang tìm: " + keyword + " (Giả lập)");
    }

    @FXML
    private void LogoutAction() {
        System.out.println("Đang xử lý đăng xuất...");
        SessionManager.logout();
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/Main.fxml", "Trang chủ");
    }
}