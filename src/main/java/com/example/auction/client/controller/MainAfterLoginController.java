package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label; // Thêm thư viện Label để hiện lời chào
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
public class MainAfterLoginController {
    @FXML private TextField txtSearch;
    @FXML private FlowPane productContainer;
    // Thêm một Label trên Scene Builder (nhớ gắn fx:id là lblWelcome) để hiện tên người dùng
    @FXML private MenuButton userMenuButton;
    @FXML
    public void initialize() {
        if (SessionManager.isLoggedIn() && userMenuButton != null) {
            userMenuButton.setText(SessionManager.getCurrentUser());
        }

        // TODO: [BACKEND] - Gọi Socket gửi lệnh yêu cầu Server trả về danh sách Item.
        // TODO: [BACKEND] - Nhận List<ItemDTO> từ Server và đổ các khối giao diện vào productContainer.
        System.out.println("Đã load giao diện trang chủ thành công. Đang chờ data từ Server...");
    }
    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText();
        if (keyword.trim().isEmpty()) {
            SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập từ khóa tìm kiếm!");
            return;
        }
        // TODO: [BACKEND] - Gửi keyword lên Server để nhờ Backend query Database và trả về danh sách lọc.
        System.out.println("Đang tìm kiếm sản phẩm với từ khóa: " + keyword);
        SceneManager.getInstance().showAlert(Alert.AlertType.INFORMATION, "Tìm kiếm", "Đang tìm: " + keyword + " (Giả lập)");
    }
    @FXML
    private void LogoutAction(){
        System.out.println("Đang xử lý đăng xuất...");
        // QUAN TRỌNG: Phải xé bỏ thẻ Session trước khi đẩy người dùng ra ngoài
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/Main.fxml", "Trang chủ");
        SessionManager.logout();
    }
    @FXML
    private void goToAddProduct() {
        System.out.println("User " + SessionManager.getCurrentUser() + " đang mở màn hình Thêm Sản Phẩm...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/addproduct-view.fxml", "Thêm Sản Phẩm Mới - Team 15");
    }
}
