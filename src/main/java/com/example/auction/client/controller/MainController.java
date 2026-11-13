package com.example.auction.client.controller;

import com.example.auction.client.view.ClockUtil;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MainController {

    @FXML private TextField txtSearch;
    @FXML private FlowPane productContainer;

    // Các nút đề mục lớn bên cột Sidebar trái
    @FXML private Button btnNavHome;
    @FXML private Button btnNavMyBids;
    @FXML private Button btnNavMyProducts;
    @FXML private Button btnNavWallet;
    @FXML private Button btnNavAccount;
    @FXML private Label lblClock; // Nhớ đặt tên trùng với fx:id ngoài SceneBuilder
    private static final String ACTIVE_BTN_STYLE =
            "-fx-background-color: #fce8e9; -fx-background-radius: 5; -fx-text-fill: #d31126; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String INACTIVE_BTN_STYLE =
            "-fx-background-color: transparent; -fx-background-radius: 5; -fx-text-fill: #4b5563; -fx-font-weight: normal; -fx-cursor: hand;";

    @FXML
    public void initialize() {
        ClockUtil.bindClockToLabel(lblClock);
        // Mặc định luôn sáng nút Trang chủ cho Khách
        setActiveSidebarButton(btnNavHome);

        // -------------------------------------------------------------
        // [MỚI THÊM] - NẠP DỮ LIỆU GIẢ VÀO KHUNG CHỨA SẢN PHẨM
        // -------------------------------------------------------------
        if (productContainer != null) {
            loadMockProducts();
        }

        // TODO: [BACKEND] - Gọi Socket lấy danh sách Item từ Server và đổ vào productContainer
        System.out.println("Đã load giao diện trang chủ (Khách) thành công. Đang chờ data từ Server...");
    }

    // =========================================================================
    // HÀM MỚI: TẠO THẺ SẢN PHẨM GIẢ (FRONTEND-FIRST)
    // =========================================================================
    private void loadMockProducts() {
        productContainer.getChildren().clear();

        // Định nghĩa 1 mảng dữ liệu giả
        String[][] mockData = {
                {"1", "Đồng hồ Rolex Submariner", "150,000,000 VND", "Đang diễn ra"},
                {"2", "iPhone 15 Pro Max 256GB", "25,000,000 VND", "Đang diễn ra"},
                {"3", "Xe máy SH 150i 2024", "95,000,000 VND", "Sắp kết thúc"},
                {"4", "MacBook Pro M3 14-inch", "38,000,000 VND", "Đang diễn ra"},
                {"5", "Voucher Du lịch Phú Quốc", "5,000,000 VND", "Đang diễn ra"},
                {"6", "Tai nghe AirPods Pro 2", "4,500,000 VND", "Sắp kết thúc"}
        };

        // Lặp qua mảng để tạo từng cái thẻ (Card)
        for (String[] data : mockData) {
            String itemId = data[0];
            String itemName = data[1];
            String currentPrice = data[2];
            String status = data[3];

            VBox card = new VBox();
            card.setPrefSize(240, 320);
            card.setSpacing(10);
            card.setPadding(new Insets(15));
            card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-cursor: hand;");

            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.rgb(0, 0, 0, 0.1));
            shadow.setRadius(10);
            shadow.setOffsetY(2);
            card.setEffect(shadow);

            VBox imagePlaceholder = new VBox();
            imagePlaceholder.setPrefSize(210, 160);
            imagePlaceholder.setAlignment(Pos.CENTER);
            imagePlaceholder.setStyle("-fx-background-color: #e5e7eb; -fx-background-radius: 8;");
            Label lblImgTxt = new Label("🖼️ Ảnh sản phẩm");
            lblImgTxt.setTextFill(Color.web("#9ca3af"));
            imagePlaceholder.getChildren().add(lblImgTxt);

            Label lblName = new Label(itemName);
            lblName.setFont(Font.font("System", FontWeight.BOLD, 15));
            lblName.setTextFill(Color.web("#1f2937"));
            lblName.setWrapText(true);

            Label lblPriceTitle = new Label("Giá hiện tại:");
            lblPriceTitle.setFont(Font.font("System", 12));
            lblPriceTitle.setTextFill(Color.web("#6b7280"));

            Label lblPrice = new Label(currentPrice);
            lblPrice.setFont(Font.font("System", FontWeight.BOLD, 16));
            lblPrice.setTextFill(Color.web("#d31126"));

            Button btnAction = new Button("Thực hiện Đấu giá");
            btnAction.setPrefWidth(210);
            btnAction.setStyle("-fx-background-color: #d31126; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

            // XỬ LÝ SỰ KIỆN: Click vào xem chi tiết
            card.setOnMouseClicked(e -> goToDetail(itemId, itemName));
            btnAction.setOnAction(e -> goToDetail(itemId, itemName));

            card.getChildren().addAll(imagePlaceholder, lblName, lblPriceTitle, lblPrice, btnAction);
            productContainer.getChildren().add(card);
        }
    }

    // =========================================================================
    // HÀM MỚI: CHUYỂN TRANG SANG CHI TIẾT SẢN PHẨM
    // =========================================================================
    private void goToDetail(String itemId, String itemName) {
        System.out.println("Đang mở chi tiết sản phẩm ID: " + itemId + " - " + itemName);
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/detail-view.fxml", "Chi tiết: " + itemName);
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