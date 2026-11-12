package com.example.auction.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.ObjectOutputStream;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
// TODO: Mở comment các import này khi ghép Backend
// import com.example.auction.client.clientnetworkmanager.AuctionClientSocket;
// import com.example.auction.shared.dto.MessageProtocol;
// import com.example.auction.shared.dto.ItemDTO;
import javafx.animation.Interpolator; // Bổ sung import này
import javafx.animation.Animation; // Bổ sung import này

public class AddProductController /* implements MessageHandler */ {

    // =========================================================================
    // PHẦN 1: LÃNH ĐỊA CỦA FRONTEND (UI COMPONENTS - CHUẨN GIAO DIỆN CỦA ÔNG)
    // =========================================================================
    @FXML private TextField itemName;
    @FXML private MenuButton menuButton;
    @FXML private TextArea infoText;
    @FXML private AnchorPane showInfo;
    @FXML private Button btnback;

    private File selectedImageFile;

    // =========================================================================
    // PHẦN 2: KHỞI TẠO VÀ ĐĂNG KÝ MẠNG
    // =========================================================================
    @FXML
    public void initialize() {
        // TODO: Đăng ký nhận tin nhắn từ Server (Mở comment khi có Backend)
        // AuctionClientSocket.getInstance().setMessageHandler(this);
        // ... (Các bước khởi tạo Image và ImageView giữ nguyên)
        Image dragonGif = new Image(getClass().getResourceAsStream("/Imagine/BoomChakalaka.gif"));
        ImageView dragon = new ImageView(dragonGif);
        dragon.setFitWidth(600);
        dragon.setFitHeight(600);

// 1. Đặt tọa độ ban đầu lùi hẳn ra ngoài màn hình bên trái
        dragon.setX(-400);
        dragon.setY(-150);

        showInfo.getChildren().add(dragon);

// 2. Cài đặt Animation bay ngang
        TranslateTransition fly = new TranslateTransition(Duration.seconds(5), dragon);
//        fly.setByX(1800); // Bay một quãng đường 1800 pixel sang phải

// 3. CÁC TÙY CHỈNH ĐỂ BAY "ĐẸP" HƠN:

// Lặp lại vô hạn (bay qua bay lại không bao giờ dừng)
        fly.setCycleCount(Animation.INDEFINITE);

// Tự động đảo chiều (bay từ trái sang phải, rồi lại từ phải sang trái)
        fly.setAutoReverse(true);

// Chỉnh gia tốc (bay mượt mà, từ từ tăng tốc rồi giảm tốc khi đến mép)
        fly.setInterpolator(Interpolator.EASE_BOTH);

// 4. Bắt đầu bay
        fly.play();
        // Tự động cập nhật chữ trên MenuButton khi chọn danh mục
        if (menuButton != null) {
            for (MenuItem item : menuButton.getItems()) {
                item.setOnAction(event -> menuButton.setText(item.getText()));
            }
        }
    }

    // =========================================================================
    // PHẦN 3: FRONTEND GỬI DỮ LIỆU CHO BACKEND (ACTION - TÁCH LUỒNG)
    // =========================================================================
    @FXML
    private void addItem() {
        // 1. Frontend lấy dữ liệu thô và validate
        String productName = itemName.getText();
        String description = infoText.getText();
        String category = menuButton.getText();
        String seller = SessionManager.getCurrentUser();

        if (productName == null || productName.trim().isEmpty()) {
            SceneManager.getInstance().showAlert(AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập tên sản phẩm!");
            return;
        }

        if (selectedImageFile == null) {
            SceneManager.getInstance().showAlert(AlertType.WARNING, "Thiếu hình ảnh", "Vui lòng chọn hình ảnh cho sản phẩm!");
            return;
        }

        // 2. Mở luồng mạng (Thread) để không làm đơ giao diện
        Thread networkThread = new Thread(() -> {
            try {
                // TODO: [BACKEND] - Mở khối code này khi ghép mạng
                /*
                synchronized (AuctionClientSocket.getInstance()) {
                    // Chuyển selectedImageFile thành byte[] và gói vào DTO
                    ItemDTO dto = new ItemDTO(productName, category, description, seller, null);
                    MessageProtocol request = new MessageProtocol("ADD_PRODUCT", dto, null, null);

                    ObjectOutputStream out = AuctionClientSocket.getInstance().getOut();
                    out.writeObject(request);
                    out.flush();
                }
                */

                // --- TEST MÔ PHỎNG FRONTEND (Không có mạng) ---
                System.out.println("Đang gửi DTO lên Server... Tên SP: " + productName);
                Thread.sleep(1500); // Giả lập chờ mạng 1.5 giây

                // Cập nhật UI phải bọc trong Platform.runLater
                Platform.runLater(() -> {
                    SceneManager.getInstance().showAlert(AlertType.INFORMATION, "Thành công", "Đăng bán sản phẩm thành công!");
                    back(); // Quay về trang chủ
                });
                // ----------------------------------------------

            } catch (Exception e) {
                Platform.runLater(() -> {
                    SceneManager.getInstance().showAlert(AlertType.ERROR, "Lỗi kết nối", "Không thể kết nối đến Server!");
                });
            }
        });
        networkThread.setDaemon(true);
        networkThread.start();
    }

    // =========================================================================
    // PHẦN 4: BACKEND TRẢ KẾT QUẢ CHO FRONTEND (CALLBACK)
    // Nơi duy nhất xử lý phản hồi từ Server (Đang comment chờ Backend)
    // =========================================================================
    /*
    @Override
    public void onMessageReceived(MessageProtocol response) {
        Platform.runLater(() -> {
            if ("SUCCESS".equals(response.status())) {
                SceneManager.getInstance().showAlert(AlertType.INFORMATION, "Thành công", "Sản phẩm đã được đưa vào hàng đợi duyệt!");
                back();
            } else if ("ERROR".equals(response.status())) {
                SceneManager.getInstance().showAlert(AlertType.ERROR, "Thất bại", response.message());
            }
        });
    }
    */

    // =========================================================================
    // CÁC HÀM XỬ LÝ SỰ KIỆN GIAO DIỆN KHÁC (UI EVENTS)
    // =========================================================================
    @FXML
    private void handlePickImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn hình ảnh cho sản phẩm");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        selectedImageFile = fileChooser.showOpenDialog(SceneManager.getInstance().getPrimaryStage());

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            ImageView imageView = new ImageView(image);

            imageView.setFitWidth(showInfo.getPrefWidth() > 0 ? showInfo.getPrefWidth() : 350);
            imageView.setFitHeight(showInfo.getPrefHeight() > 0 ? showInfo.getPrefHeight() : 350);
            imageView.setPreserveRatio(true);

            showInfo.getChildren().clear();
            showInfo.getChildren().add(imageView);
        }
    }

    @FXML
    private void back() {
        System.out.println("Quay lại màn hình Trang chủ...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/MainAfterLogin.fxml", "Trang chủ hệ thống - Team 15");
    }
}