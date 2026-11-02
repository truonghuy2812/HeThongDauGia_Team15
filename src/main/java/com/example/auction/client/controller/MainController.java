package com.example.auction.client.controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import java.io.IOException;
public class MainController {
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
    private void goToLogin() {
        System.out.println("Chuyển hướng sang màn hình Đăng Nhập...");
        switchScene("/com/example/auction/client/view/login-view.fxml", "Đăng nhập hệ thống", 1000, 600);
    }

    @FXML
    private void goToSignup() {
        System.out.println("Chuyển hướng sang màn hình Đăng Ký...");
        // Gọi file signup.fxml như cấu trúc bạn đang có
        switchScene("/com/example/auction/client/view/signup.fxml", "Đăng ký tài khoản mới", 1000, 600);
    }
    private void switchScene(String fxmlPath, String title, double width, double height) {
        try {
            // Lấy Stage hiện tại từ thanh search (hoặc bất kỳ component nào đang hiển thị)
            Stage stage = (Stage) txtSearch.getScene().getWindow();
            // Chú ý: Nếu nhóm bạn có file SceneManager chuyên xử lý chuyển trang,
            // bạn có thể xóa đoạn FXMLLoader dưới đây và gọi hàm của SceneManager ở đây nhé!
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(fxmlLoader.load(), width, height);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi điều hướng", "Không thể tải màn hình: " + fxmlPath);
        }
    }
    @FXML
    private void goToAddProduct() {
        if (!SessionManager.isLoggedIn()) {
            // Nếu chưa: Hiện thông báo và đẩy sang màn hình Login
            showAlert(Alert.AlertType.WARNING, "Yêu cầu đăng nhập", "Bạn phải đăng nhập thì mới được đăng bán sản phẩm!");
            System.out.println("Chưa đăng nhập. Đang chuyển hướng sang trang Login...");
            switchScene("/com/example/auction/client/view/login-view.fxml", "Đăng nhập hệ thống", 1000, 600);
            return; // Lệnh return này cực kỳ quan trọng để code dừng lại, KHÔNG chạy xuống dòng switchScene Add Product bên dưới
        }
        // Nếu đã đăng nhập (isLoggedIn() == true): Cho phép vào màn hình Đăng bán
        System.out.println("User " + SessionManager.getCurrentUser() + " đang mở màn hình Thêm Sản Phẩm...");
        switchScene("/com/example/auction/client/view/addproduct-view.fxml", "Đăng bán sản phẩm", 1000, 600);
    }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}