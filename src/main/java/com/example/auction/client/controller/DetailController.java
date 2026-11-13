package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DetailController {

    // Các thành phần giao diện hiển thị thông tin sản phẩm
    @FXML private Label lblProductName;
    @FXML private Label lblCategory;
    @FXML private Label lblSellerName;
    @FXML private Label lblDescription;
    @FXML private Label lblCurrentPrice;
    @FXML private Label lblTimeRemaining;
    @FXML private ImageView imgProduct;

    // Khu vực đặt giá (Bid)
    @FXML private TextField txtBidAmount;
    @FXML private Button btnPlaceBid;
    @FXML private Button btnAutoBid;

    // Biến lưu trữ ID của sản phẩm đang xem để gửi lên Server
    private int currentProductId;

    @FXML
    public void initialize() {

        System.out.println("Đã load màn hình Chi tiết sản phẩm.");
        // TODO: [BACKEND] - Nhận ID sản phẩm từ màn hình trước truyền sang.
        // TODO: [BACKEND] - Gọi Socket lấy dữ liệu chi tiết của sản phẩm theo ID và đổ vào các Label, ImageView.

        // Giả lập dữ liệu hiển thị ban đầu
        lblProductName.setText("Laptop Dell Vostro 3400 (Giả lập)");
        lblCategory.setText("Đồ điện tử");
        lblSellerName.setText("Đinh Nguyễn Trường Huy");
        lblCurrentPrice.setText("5,000,000 VNĐ");
        lblTimeRemaining.setText("02:15:30");
    }

    /**
     * Xử lý khi người dùng bấm nút Đặt giá thủ công (Place Bid)
     */
    @FXML
    private void handlePlaceBid() {
        if (!SessionManager.isLoggedIn()) {
            SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Yêu cầu đăng nhập", "Bạn cần đăng nhập để tham gia đấu giá!");
            return;
        }

        String bidAmountStr = txtBidAmount.getText();
        if (bidAmountStr == null || bidAmountStr.trim().isEmpty()) {
            SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Nhập thiếu", "Vui lòng nhập số tiền bạn muốn đấu giá.");
            return;
        }

        try {
            double bidAmount = Double.parseDouble(bidAmountStr);
            // TODO: [BACKEND] - So sánh bidAmount với giá hiện tại của sản phẩm (currentPrice).
            // Nếu bidAmount <= currentPrice, báo lỗi. Nếu hợp lệ thì gửi yêu cầu lên Server.

            System.out.println("User " + SessionManager.getCurrentUser() + " đang đặt giá " + bidAmount + " cho sản phẩm ID: " + currentProductId);
            SceneManager.getInstance().showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã gửi yêu cầu đặt giá: " + bidAmount + " (Chờ Server xử lý)");

            // Xóa trắng ô nhập liệu sau khi đặt
            txtBidAmount.clear();

        } catch (NumberFormatException e) {
            SceneManager.getInstance().showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Vui lòng nhập số tiền hợp lệ (không chứa chữ cái).");
        }
    }

    /**
     * Mở form thiết lập Đặt giá tự động (Auto Bid)
     */
    @FXML
    private void openAutoBidForm() {
        if (!SessionManager.isLoggedIn()) {
            SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Yêu cầu đăng nhập", "Bạn cần đăng nhập để thiết lập đấu giá tự động!");
            return;
        }

        System.out.println("Mở form cấu hình Đặt giá tự động cho sản phẩm ID: " + currentProductId);
        // Lưu ý: Thường Auto Bid sẽ mở dạng Popup (nhỏ) đè lên trên màn hình chi tiết,
        // hoặc chuyển hẳn sang trang auto-bid-form.fxml tùy thiết kế UI của bạn.
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/auto-bid-form.fxml", "Thiết lập Đấu giá tự động - Team 15");
    }

    /**
     * Nút Quay lại màn hình Trang chủ
     */
    @FXML
    private void backToMain() {
        System.out.println("Quay lại Trang chủ...");
        if (SessionManager.isLoggedIn()) {
            SceneManager.getInstance().switchScene("/com/example/auction/client/view/MainAfterLogin.fxml", "Trang chủ hệ thống - Team 15");
        } else {
            SceneManager.getInstance().switchScene("/com/example/auction/client/view/Main.fxml", "Trang chủ hệ thống - Team 15");
        }
    }
}
