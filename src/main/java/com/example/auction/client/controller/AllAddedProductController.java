package com.example.auction.client.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AllAddedProductController {

    // --- Khai báo thanh Header ---
    @FXML private TextField txtSearchMyProduct;
    @FXML private MenuButton userMenuButton;

    // --- Khai báo Bảng Sản phẩm ---
    @FXML private TableView<ProductViewModel> tblMyProducts;
    @FXML private TableColumn<ProductViewModel, Integer> colId;
    @FXML private TableColumn<ProductViewModel, String> colName;
    @FXML private TableColumn<ProductViewModel, String> colCategory;
    @FXML private TableColumn<ProductViewModel, Double> colStartPrice;
    @FXML private TableColumn<ProductViewModel, Double> colCurrentPrice;
    @FXML private TableColumn<ProductViewModel, String> colStatus;

    // Danh sách lưu trữ dữ liệu hiển thị trên bảng
    private ObservableList<ProductViewModel> productList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("Đã load màn hình Quản lý Sản phẩm của tôi.");

        // Cập nhật tên người dùng trên góc phải nếu đã login
        if (SessionManager.isLoggedIn() && userMenuButton != null) {
            userMenuButton.setText(SessionManager.getCurrentUser());
        }

        setupTableColumns();
        loadDummyData(); // TODO: [BACKEND] - Thay bằng API gọi Server lấy danh sách sản phẩm của currentUser
    }

    /**
     * Cấu hình các cột của TableView liên kết với thuộc tính của ProductViewModel
     */
    private void setupTableColumns() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colCategory.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

        // Định dạng tiền tệ cho cột Giá
        colStartPrice.setCellValueFactory(cellData -> cellData.getValue().startPriceProperty().asObject());
        colStartPrice.setCellFactory(tc -> new javafx.scene.control.TableCell<ProductViewModel, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f VNĐ", price));
                }
            }
        });

        colCurrentPrice.setCellValueFactory(cellData -> cellData.getValue().currentPriceProperty().asObject());
        colCurrentPrice.setCellFactory(tc -> new javafx.scene.control.TableCell<ProductViewModel, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f VNĐ", price));
                }
            }
        });

        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
    }

    /**
     * Nạp dữ liệu giả lập để test giao diện
     */
    private void loadDummyData() {
        productList.clear();
        productList.add(new ProductViewModel(101, "Laptop Dell Vostro 3400", "Đồ điện tử", 5000000.0, 5500000.0, "Đang đấu giá"));
        productList.add(new ProductViewModel(102, "Điện thoại iPhone 13", "Đồ điện tử", 12000000.0, 12000000.0, "Chờ duyệt"));
        productList.add(new ProductViewModel(103, "Đồng hồ Casio G-Shock", "Hàng sưu tầm", 1500000.0, 2100000.0, "Đã bán"));
        productList.add(new ProductViewModel(104, "Áo khoác nam mùa đông", "Thời trang", 300000.0, 450000.0, "Đang đấu giá"));
        productList.add(new ProductViewModel(105, "Xe đạp thể thao", "Xe cộ & Khác", 2000000.0, 2000000.0, "Đã hủy"));

        tblMyProducts.setItems(productList);
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
        // TODO: Màn hình Đấu giá của tôi
        System.out.println("Chuyển sang màn hình Đấu giá của tôi...");
    }

    @FXML
    private void goToWallet() {
        // TODO: Màn hình Ví & Nạp tiền (PaymentController)
        System.out.println("Chuyển sang màn hình Ví & Nạp tiền...");
    }

    @FXML
    private void goToAccount() {
        // TODO: Màn hình Quản lý Tài khoản cá nhân
        System.out.println("Chuyển sang màn hình Tài khoản...");
    }

    @FXML
    private void goToAddProduct() {
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/addproduct-view.fxml", "Đăng bán sản phẩm mới - Team 15");
    }

    @FXML
    private void LogoutAction() {
        SessionManager.logout();
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/Main.fxml", "Trang chủ hệ thống - Team 15");
    }

    // ==========================================
    // LỚP VIEWMODEL NỘI BỘ HỖ TRỢ TABLEVIEW
    // ==========================================
    public static class ProductViewModel {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty category;
        private final SimpleDoubleProperty startPrice;
        private final SimpleDoubleProperty currentPrice;
        private final SimpleStringProperty status;

        public ProductViewModel(int id, String name, String category, double startPrice, double currentPrice, String status) {
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.category = new SimpleStringProperty(category);
            this.startPrice = new SimpleDoubleProperty(startPrice);
            this.currentPrice = new SimpleDoubleProperty(currentPrice);
            this.status = new SimpleStringProperty(status);
        }

        public SimpleIntegerProperty idProperty() { return id; }
        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty categoryProperty() { return category; }
        public SimpleDoubleProperty startPriceProperty() { return startPrice; }
        public SimpleDoubleProperty currentPriceProperty() { return currentPrice; }
        public SimpleStringProperty statusProperty() { return status; }
    }
}