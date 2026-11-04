package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AdminDepositManager {

    // --- CÁC THÀNH PHẦN GIAO DIỆN (UI COMPONENTS) ---
    @FXML private TableView<?> tblPendingList; // Dấu ? tạm thời thay cho Model Class (ví dụ: DepositDTO)
    @FXML private TableColumn<?, ?> colId;
    @FXML private TableColumn<?, ?> colAmount;
    @FXML private TableColumn<?, ?> colTime;

    @FXML private Button btnApprove;
    @FXML private Button btnLogOut;
    @FXML private Label lblNotification;

    @FXML
    public void initialize() {
        // Gắn sự kiện cho nút Đăng Xuất
        btnLogOut.setOnAction(event -> handleLogout());

        // Gắn sự kiện cho nút Phê Duyệt
        btnApprove.setOnAction(event -> approveDeposit());

        // TODO: [BACKEND] - Gọi API lấy danh sách giao dịch nạp tiền và đổ vào tblPendingList
    }

    // --- CÁC HÀM XỬ LÝ SỰ KIỆN (ACTIONS) ---

    @FXML
    private void manageApproveAuctions() {
        System.out.println("Chuyển về màn hình Duyệt Đấu Giá...");
        // Đảm bảo tên file FXML ở dưới khớp với tên file thật trong thư mục view của bạn
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/AdminController.fxml", "Quản lý Đấu Giá - Admin");
    }

    private void handleLogout() {
        System.out.println("Admin đang đăng xuất...");
        // SessionManager.logout();

        // Chuyển về màn MainAfterLogin
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/Main.fxml", "Trang chủ hệ thống");
    }

    private void approveDeposit() {
        // TODO: [BACKEND] - Lấy item đang được chọn trong TableView, gọi API cộng tiền cho User
        lblNotification.setText("✔ Đã duyệt cộng tiền thành công!");
    }
}