package com.example.auction.client.controller;

import com.example.auction.client.clientnetworkmanager.AuctionClientSocket;
import com.example.auction.client.clientnetworkmanager.MessageHandler;
import com.example.auction.client.view.ClockUtil;
import com.example.auction.shared.dto.MessageProtocol;
//import com.example.auction.shared.dto.DepositDTO; // DTO ví dụ, nhớ check lại với Backend
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;

import java.io.ObjectOutputStream;

public class PaymentController implements MessageHandler {

    // Khai báo các thành phần trên Header
    @FXML private MenuButton userMenuButton;

    // Khai báo các thành phần trong khu vực Ví & Nạp tiền
    @FXML private Label lblBalance;
    @FXML private TextField txtDepositAmount;
    @FXML private Label lblClock;
    @FXML
    public void initialize() {
        ClockUtil.bindClockToLabel(lblClock);
        System.out.println("Đã load màn hình Ví & Nạp tiền.");

        // BẮT BUỘC: Đăng ký nghe Socket cho màn hình này
        AuctionClientSocket.getInstance().setMessageHandler(this);

        // Hiển thị tên người dùng nếu đã đăng nhập
        if (SessionManager.isLoggedIn() && userMenuButton != null) {
            userMenuButton.setText(SessionManager.getCurrentUser());
        }

        // Cập nhật số dư (Tạm thời giả lập, sau này lấy từ Backend)
        updateBalanceDisplay(0.0);
    }

    private void updateBalanceDisplay(double balance) {
        if (lblBalance != null) {
            lblBalance.setText(String.format("%,.0f VNĐ", balance));
        }
    }

    @FXML
    private void handleDepositRequest() {
        String amountStr = txtDepositAmount.getText();

        if (amountStr == null || amountStr.trim().isEmpty()) {
            SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập số tiền bạn muốn nạp.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Không hợp lệ", "Số tiền nạp phải lớn hơn 0.");
                return;
            }

            // GỬI LÊN SERVER BẰNG THREAD
            /*Thread networkThread = new Thread(() -> {
                try {
                    synchronized (AuctionClientSocket.getInstance()) {
                        ObjectOutputStream out = AuctionClientSocket.getInstance().getOut();

                        // Đóng gói dữ liệu nạp tiền gửi đi
                        DepositDTO depositDTO = new DepositDTO(SessionManager.getCurrentUser(), amount);
                        MessageProtocol request = new MessageProtocol("REQUEST_DEPOSIT", depositDTO, null, null);

                        out.writeObject(request);
                        out.flush();
                    }
                } catch (Exception e) {
                    Platform.runLater(() ->
                            SceneManager.getInstance().showAlert(Alert.AlertType.ERROR, "Lỗi mạng", "Không kết nối được Server!")
                    );
                }
            });*/
          /*  networkThread.setDaemon(true);
            networkThread.start();*/

            // Xóa trắng ô nhập liệu
            txtDepositAmount.clear();

        } catch (NumberFormatException e) {
            SceneManager.getInstance().showAlert(Alert.AlertType.ERROR, "Sai định dạng", "Vui lòng chỉ nhập số.");
        }
    }
    public void goToAddProduct(){
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/addproduct-view.fxml", "Thêm Sản Phẩm Đấu Giá - Team 15");
    }
    // HÀM HỨNG DỮ LIỆU TỪ SERVER TRẢ VỀ
    @Override
    public void onMessageReceived(MessageProtocol response) {
        Platform.runLater(() -> {
            if ("SUCCESS".equals(response.status())) {
                SceneManager.getInstance().showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã gửi yêu cầu nạp tiền! Vui lòng chờ Admin duyệt.");
            } else if ("ERROR".equals(response.status())) {
                SceneManager.getInstance().showAlert(Alert.AlertType.ERROR, "Thất bại", response.message());
            }
        });
    }

    // ==========================================
    // CÁC HÀM ĐIỀU HƯỚNG SIDEBAR (ĐÃ GIỮ NGUYÊN 100%)
    // ==========================================

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
    private void goToAccount() {
        System.out.println("Chuyển sang màn hình Tài khoản cá nhân...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/account-view.fxml", "Tài khoản của tôi - Team 15");
    }

    @FXML
    private void LogoutAction() {
        System.out.println("User " + SessionManager.getCurrentUser() + " đang đăng xuất...");
        SessionManager.logout();
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/Main.fxml", "Trang chủ hệ thống - Team 15");
    }
}