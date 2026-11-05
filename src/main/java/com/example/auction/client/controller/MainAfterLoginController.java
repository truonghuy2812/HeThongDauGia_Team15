package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainAfterLoginController {

    @FXML private TextField txtSearch;
    @FXML private FlowPane productContainer;
    @FXML private MenuButton userMenuButton;

    // Các nút đề mục lớn bên cột Sidebar trái
    @FXML private Button btnNavHome;
    @FXML private Button btnNavMyBids;
    @FXML private Button btnNavMyProducts;
    @FXML private Button btnNavWallet;
    @FXML private Button btnNavAccount;

    // 2 vùng giao diện chính ở giữa (Trang chủ / Tài khoản)
    @FXML private VBox homePane;
    @FXML private VBox accountPane;
    @FXML private Label lblSectionTitle;

    // Các thông tin hiển thị trong mục Tài khoản của tôi
    @FXML private Label lblAccountUsername;
    @FXML private TextField txtProfileUsername;
    @FXML private TextField txtProfileEmail;

    private static final String ACTIVE_BTN_STYLE =
            "-fx-background-color: #fce8e9; -fx-background-radius: 5; -fx-text-fill: #d31126; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String INACTIVE_BTN_STYLE =
            "-fx-background-color: transparent; -fx-background-radius: 5; -fx-text-fill: #4b5563; -fx-font-weight: normal; -fx-cursor: hand;";

    @FXML
    public void initialize() {
        if (SessionManager.isLoggedIn()) {
            String currentUser = SessionManager.getCurrentUser();
            if (userMenuButton != null) {
                userMenuButton.setText(currentUser);
            }
            if (lblAccountUsername != null) {
                lblAccountUsername.setText(currentUser);
            }
            if (txtProfileUsername != null) {
                txtProfileUsername.setText(currentUser);
            }
            if (txtProfileEmail != null) {
                txtProfileEmail.setText(currentUser + "@auction.com");
            }
        }

        // Mặc định khi mới vào sẽ chọn mục Trang chủ
        showHomeView();

        // TODO: [BACKEND] - Gọi Socket gửi lệnh yêu cầu Server trả về danh sách Item.
        // TODO: [BACKEND] - Nhận List<ItemDTO> từ Server và đổ các khối giao diện vào productContainer.
        System.out.println("Đã load giao diện trang chủ thành công. Đang chờ data từ Server...");
    }

    // Hàm đổi màu nút đang được chọn bên thanh Sidebar trái
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
        if (lblSectionTitle != null) {
            lblSectionTitle.setText("Sản phẩm Đấu giá nổi bật");
        }
        if (homePane != null && accountPane != null) {
            homePane.setVisible(true);
            homePane.setManaged(true);
            accountPane.setVisible(false);
            accountPane.setManaged(false);
        }
    }

    // 2. Chuyển về mục Đấu giá của tôi
    @FXML
    private void showMyBidsView() {
        setActiveSidebarButton(btnNavMyBids);
        if (lblSectionTitle != null) {
            lblSectionTitle.setText("Các phiên Đấu giá tôi đang tham gia");
        }
        if (homePane != null && accountPane != null) {
            homePane.setVisible(true);
            homePane.setManaged(true);
            accountPane.setVisible(false);
            accountPane.setManaged(false);
        }
    }
/*    @FXML
    private Button button;*/
    // 3. Chuyển về mục Sản phẩm của tôi
    @FXML
    private void showMyProductsView() {
        setActiveSidebarButton(btnNavMyProducts);
        if (lblSectionTitle != null) {
            lblSectionTitle.setText("Sản phẩm tôi đã đăng bán");
        }
        if (homePane != null && accountPane != null) {
            homePane.setVisible(true);
            homePane.setManaged(true);
            accountPane.setVisible(false);
            accountPane.setManaged(false);
        }

        /*Stage curStage = (Stage) button.getScene().getWindow();
        SceneManager.getInstance().switchTo(curStage,"/com/example/auction/client/view/all-added-product.fxml");*/
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/all-added-product.fxml", "");
    }

    // 4. Chuyển về mục Ví & Nạp tiền
    @FXML
    private void showWalletView() {
        setActiveSidebarButton(btnNavWallet);
        if (homePane != null && accountPane != null) {
            homePane.setVisible(false);
            homePane.setManaged(false);
            accountPane.setVisible(true);
            accountPane.setManaged(true);
        }
    }

    // 5. Khi bấm "Tài khoản" ở góc phải trên cùng HOẶC bấm "Tài khoản của tôi" bên trái
    @FXML
    private void goToAccount() {
        System.out.println("Chuyển sang mục Tài khoản của tôi...");
        setActiveSidebarButton(btnNavAccount);
        if (homePane != null && accountPane != null) {
            homePane.setVisible(false);
            homePane.setManaged(false);
            accountPane.setVisible(true);
            accountPane.setManaged(true);
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText();
        if (keyword.trim().isEmpty()) {
            SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập từ khóa tìm kiếm!");
            return;
        }
        showHomeView();
        // TODO: [BACKEND] - Gửi keyword lên Server để nhờ Backend query Database và trả về danh sách lọc.
        System.out.println("Đang tìm kiếm sản phẩm với từ khóa: " + keyword);
        SceneManager.getInstance().showAlert(Alert.AlertType.INFORMATION, "Tìm kiếm", "Đang tìm: " + keyword + " (Giả lập)");
    }

    @FXML
    private void LogoutAction() {
        System.out.println("Đang xử lý đăng xuất...");
        SessionManager.logout();
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/Main.fxml", "Trang chủ");
    }

    @FXML
    private void goToAddProduct() {
        System.out.println("User " + SessionManager.getCurrentUser() + " đang mở màn hình Thêm Sản Phẩm...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/addproduct-view.fxml", "Thêm Sản Phẩm Mới - Team 15");
    }
}