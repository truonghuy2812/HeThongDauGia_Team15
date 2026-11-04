package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AdminController {

    // --- CÁC THÀNH PHẦN GIAO DIỆN (UI COMPONENTS) ---
    @FXML private TableView<?> tblPendingAuctions; // Dấu ? tạm thời thay cho Model Class (ví dụ: AuctionDTO)
    @FXML private TableColumn<?, ?> colId;
    @FXML private TableColumn<?, ?> colItemName;
    @FXML private TableColumn<?, ?> colOwner;
    @FXML private TableColumn<?, ?> colStartPrice;
    @FXML private TableColumn<?, ?> colStatus;

    @FXML private Button btnApprove;
    @FXML private Button btnLogOut;
    @FXML private Label lblNotification;

    @FXML
    public void initialize() {
        // Gắn sự kiện cho nút Đăng Xuất
        btnLogOut.setOnAction(event -> handleLogout());

        // Gắn sự kiện cho nút Phê Duyệt
        btnApprove.setOnAction(event -> approveAuction());

        // TODO: [BACKEND] - Gọi API lấy danh sách phiên đấu giá chờ duyệt và đổ vào tblPendingAuctions
    }

    // --- CÁC HÀM XỬ LÝ SỰ KIỆN (ACTIONS) ---

    @FXML
    private void manageDeposit() {
        System.out.println("Chuyển sang màn hình Duyệt Nạp Tiền...");
        // Đảm bảo tên file FXML ở dưới khớp với tên file thật trong thư mục view của bạn
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/AdminDepositManager.fxml", "Quản lý Nạp Tiền - Admin");
    }

    private void handleLogout() {
        System.out.println("Admin đang đăng xuất...");
        // Xóa thông tin phiên đăng nhập (nếu có hàm logout trong SessionManager)
        // SessionManager.logout();

        // Chuyển về màn MainAfterLogin theo đúng yêu cầu
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/Main.fxml", "Trang chủ hệ thống");
    }

    private void approveAuction() {
        // TODO: [BACKEND] - Lấy item đang được chọn trong TableView, gọi API duyệt, và cập nhật lại bảng
        lblNotification.setText("✔ Đã duyệt phiên đấu giá thành công!");
    }
}