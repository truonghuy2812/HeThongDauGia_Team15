package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.application.Platform;

public class PaymentController {

    // Khai báo các thành phần trên Header
    @FXML private MenuButton userMenuButton;

    // Khai báo các thành phần trong khu vực Ví & Nạp tiền
    @FXML private Label lblBalance;
    @FXML private TextField txtDepositAmount;

    @FXML
    public void initialize() {
        System.out.println("Đã load màn hình Ví & Nạp tiền.");

        // Hiển thị tên người dùng nếu đã đăng nhập
        if (SessionManager.isLoggedIn() && userMenuButton != null) {
            userMenuButton.setText(SessionManager.getCurrentUser());
        }

        // Cập nhật số dư (Tạm thời giả lập, sau này lấy từ Backend)
        updateBalanceDisplay(0.0);
        // TODO: [BACKEND] - Gọi API lấy số dư thực tế của user hiện tại từ Database và gọi updateBalanceDisplay(soDu)
    }

    /**
     * Hàm hỗ trợ hiển thị số dư định dạng đẹp (VD: 5,000,000 VNĐ)
     */
    private void updateBalanceDisplay(double balance) {
        if (lblBalance != null) {
            lblBalance.setText(String.format("%,.0f VNĐ", balance));
        }
    }

    /**
     * Xử lý sự kiện khi người dùng bấm nút "Tạo yêu cầu nạp tiền"
     */
    @FXML
    private void handleDepositRequest() {
        String amountStr = txtDepositAmount.getText();

        // 1. Kiểm tra rỗng
        if (amountStr == null || amountStr.trim().isEmpty()) {
            SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập số tiền bạn muốn nạp.");
            return;
        }

        try {
            // 2. Chuyển đổi sang số thực
            double amount = Double.parseDouble(amountStr);

            // 3. Kiểm tra số âm hoặc bằng 0
            if (amount <= 0) {
                SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Không hợp lệ", "Số tiền nạp phải lớn hơn 0.");
                return;
            }

            // 4. Xử lý nạp tiền (Gửi lên Server)
            String currentUser = SessionManager.getCurrentUser();
            System.out.println("User [" + currentUser + "] yêu cầu nạp: " + amount + " VNĐ.");

            // TODO: [BACKEND] - Đóng gói thông tin (currentUser, amount) thành DTO và gửi qua Socket trong Thread mới.

            SceneManager.getInstance().showAlert(Alert.AlertType.INFORMATION, "Thành công",
                    "Đã gửi yêu cầu nạp " + String.format("%,.0f", amount) + " VNĐ.\nVui lòng chờ Quản trị viên (Admin) phê duyệt.");

            // Xóa trắng ô nhập liệu sau khi thành công
            txtDepositAmount.clear();

        } catch (NumberFormatException e) {
            // Lỗi khi nhập chữ hoặc ký tự đặc biệt thay vì số
            SceneManager.getInstance().showAlert(Alert.AlertType.ERROR, "Sai định dạng", "Vui lòng chỉ nhập số (không chứa chữ cái hay ký tự đặc biệt).");
        }
    }

    // ==========================================
    // CÁC HÀM ĐIỀU HƯỚNG SIDEBAR
    // ==========================================

    @FXML
    private void goToMain() {
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/MainAfterLogin.fxml", "Trang chủ hệ thống - Team 15");
    }

    @FXML
    private void goToMyBids() {
        // Gọi đến màn hình Đấu giá của tôi
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/my-bids-view.fxml", "Đấu giá của tôi - Team 15");
    }

    @FXML
    private void goToMyProducts() {
        // CẬP NHẬT: Đã đổi tên hàm và FXML để gọi tới MyProductController thay vì AllAddedProduct
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/my-product.fxml", "Sản phẩm của tôi - Team 15");
    }

    @FXML
    private void goToAccount() {
        System.out.println("Chuyển sang màn hình Tài khoản cá nhân...");
        // SceneManager.getInstance().switchScene("/com/example/auction/client/view/account-view.fxml", "Tài khoản của tôi - Team 15");
    }

    @FXML
    private void LogoutAction() {
        System.out.println("User " + SessionManager.getCurrentUser() + " đang đăng xuất...");
        SessionManager.logout();
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/Main.fxml", "Trang chủ hệ thống - Team 15");
    }
}