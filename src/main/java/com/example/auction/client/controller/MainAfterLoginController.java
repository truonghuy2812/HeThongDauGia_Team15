package com.example.auction.client.controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import java.io.IOException;
public class MainAfterLoginController {
    @FXML
    private TextField txtSearch;
    @FXML
    private FlowPane productContainer;
    @FXML
    public void initialize() {
        // Hàm này chạy ngay khi màn hình Main bật lên
        // TODO: Chỗ này sau sẽ gọi Socket để lấy danh sách Item từ Server và đổ vào productContainer
        System.out.println("Đã load giao diện trang chủ thành công. Đang chờ data từ Server...");
    }
    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText();
        if (keyword.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập từ khóa tìm kiếm!");
            return;
        }
        // TODO: Gửi keyword lên server để lọc sản phẩm
        System.out.println("Đang tìm kiếm sản phẩm với từ khóa: " + keyword);
        showAlert(Alert.AlertType.INFORMATION, "Tìm kiếm", "Đang tìm: " + keyword + " (Giả lập)");
    }
    @FXML
    private void LogoutAction(){
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/Main.fxml", "Trang chủ");
        SessionManager.logout();
    }
    @FXML
    private void goToAddProduct() {
        System.out.println("User " + SessionManager.getCurrentUser() + " đang mở màn hình Thêm Sản Phẩm...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/addproduct-view.fxml", "Trang chủ");
    }
    /**
     * Hàm dùng chung để chuyển màn hình (Tái sử dụng cho code gọn)
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
