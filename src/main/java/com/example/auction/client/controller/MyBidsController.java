package com.example.auction.client.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;

public class MyBidsController /* implements MessageHandler */ {

    // =========================================================================
    // PHẦN 1: LÃNH ĐỊA CỦA FRONTEND (UI COMPONENTS)
    // =========================================================================
    @FXML private MenuButton userMenuButton;

    @FXML private TableView<BidViewModel> tblMyBids;
    @FXML private TableColumn<BidViewModel, Integer> colId;
    @FXML private TableColumn<BidViewModel, String> colProductName;
    @FXML private TableColumn<BidViewModel, Double> colMyBidPrice;
    @FXML private TableColumn<BidViewModel, Double> colCurrentPrice;
    @FXML private TableColumn<BidViewModel, String> colStatus;

    private ObservableList<BidViewModel> bidList = FXCollections.observableArrayList();

    // =========================================================================
    // PHẦN 2: KHỞI TẠO VÀ ĐĂNG KÝ MẠNG
    // =========================================================================
    @FXML
    public void initialize() {
        System.out.println("Đã load màn hình Đấu giá của tôi.");

        // TODO: Đăng ký nhận tin nhắn từ Server (Mở comment khi ghép Backend)
        // AuctionClientSocket.getInstance().setMessageHandler(this);

        if (SessionManager.isLoggedIn() && userMenuButton != null) {
            userMenuButton.setText(SessionManager.getCurrentUser());
        }

        setupTableColumns();
        loadMyBidsData();
    }

    /**
     * Cấu hình cách hiển thị dữ liệu cho từng cột trong TableView
     */
    private void setupTableColumns() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colProductName.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());

        // Format tiền tệ cho cột Giá tôi đặt
        colMyBidPrice.setCellValueFactory(cellData -> cellData.getValue().myBidPriceProperty().asObject());
        colMyBidPrice.setCellFactory(tc -> new TableCell<BidViewModel, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f VNĐ", price));
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Format tiền tệ cho cột Giá cao nhất hiện tại
        colCurrentPrice.setCellValueFactory(cellData -> cellData.getValue().currentPriceProperty().asObject());
        colCurrentPrice.setCellFactory(tc -> new TableCell<BidViewModel, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f VNĐ", price));
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Tô màu Trạng thái (Xanh lá: Dẫn đầu/Thắng, Đỏ: Bị vượt)
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colStatus.setCellFactory(tc -> new TableCell<BidViewModel, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    setAlignment(Pos.CENTER);
                    String lower = status.toLowerCase();
                    if (lower.contains("dẫn đầu") || lower.contains("đã thắng")) {
                        setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;"); // Xanh lá
                    } else if (lower.contains("bị vượt giá")) {
                        setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;"); // Đỏ
                    } else {
                        setStyle("-fx-text-fill: #4b5563;"); // Xám
                    }
                }
            }
        });
    }

    // =========================================================================
    // PHẦN 3: FRONTEND YÊU CẦU DATA TỪ BACKEND
    // =========================================================================
    private void loadMyBidsData() {
        // TODO: [BACKEND] - Tạo Request DTO yêu cầu Server trả về danh sách lịch sử đấu giá của currentUser

        // --- Dữ liệu Giả lập (Test UI khi chưa có Backend) ---
        bidList.clear();
        bidList.add(new BidViewModel(201, "Laptop Dell Vostro 3400", 5500000.0, 5500000.0, "Đang dẫn đầu"));
        bidList.add(new BidViewModel(205, "Đồng hồ G-Shock", 1200000.0, 1500000.0, "Bị vượt giá"));
        bidList.add(new BidViewModel(208, "Bàn phím cơ DareU", 850000.0, 850000.0, "Đã thắng"));
        tblMyBids.setItems(bidList);
    }

    // =========================================================================
    // PHẦN 4: BACKEND TRẢ KẾT QUẢ (CALLBACK)
    // =========================================================================
    /*
    @Override
    public void onMessageReceived(MessageProtocol response) {
        Platform.runLater(() -> {
            if ("SUCCESS".equals(response.status())) {
                // Parse danh sách BidDTO từ response.data() và đưa vào mảng bidList
            }
        });
    }
    */

    // =========================================================================
    // CÁC HÀM ĐIỀU HƯỚNG SIDEBAR
    // =========================================================================
    @FXML
    private void goToMain() {
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/MainAfterLogin.fxml", "Trang chủ hệ thống - Team 15");
    }

    @FXML
    private void goToMyProducts() {
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/my-product.fxml", "Sản phẩm của tôi - Team 15");
    }

    @FXML
    private void goToWallet() {
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/payment-view.fxml", "Ví và Nạp tiền - Team 15");
    }

    @FXML
    private void goToAccount() {
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/account-view.fxml", "Tài khoản của tôi - Team 15");
    }

    @FXML
    private void goToAddProduct() {
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/addproduct-view.fxml", "Đăng bán sản phẩm - Team 15");
    }

    @FXML
    private void LogoutAction() {
        SessionManager.logout();
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/Main.fxml", "Trang chủ hệ thống - Team 15");
    }

    // =========================================================================
    // LỚP VIEWMODEL NỘI BỘ (Chỉ dùng cho TableView UI)
    // =========================================================================
    public static class BidViewModel {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty productName;
        private final SimpleDoubleProperty myBidPrice;
        private final SimpleDoubleProperty currentPrice;
        private final SimpleStringProperty status;

        public BidViewModel(int id, String productName, double myBidPrice, double currentPrice, String status) {
            this.id = new SimpleIntegerProperty(id);
            this.productName = new SimpleStringProperty(productName);
            this.myBidPrice = new SimpleDoubleProperty(myBidPrice);
            this.currentPrice = new SimpleDoubleProperty(currentPrice);
            this.status = new SimpleStringProperty(status);
        }

        public SimpleIntegerProperty idProperty() { return id; }
        public SimpleStringProperty productNameProperty() { return productName; }
        public SimpleDoubleProperty myBidPriceProperty() { return myBidPrice; }
        public SimpleDoubleProperty currentPriceProperty() { return currentPrice; }
        public SimpleStringProperty statusProperty() { return status; }
    }
}