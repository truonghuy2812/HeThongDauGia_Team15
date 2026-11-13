package com.example.auction.client.view;
import atlantafx.base.theme.PrimerLight;
import com.example.auction.client.controller.SoundManager;
import javafx.application.Application;
import com.example.auction.client.controller.SceneManager;
import javafx.stage.Stage;

// Tạm thời comment dòng import Socket để không bị lỗi nếu ông lỡ xóa
// import com.example.auction.client.clientnetworkmanager.AuctionClientSocket;

public class AuctionClientApp extends Application {
    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        SceneManager.getInstance().setPrimaryStage(stage);

        SoundManager.getInstance().playBGM("lofi_bgm.mp3");
        // ==========================================================
        // 1. TẠM TẮT LUỒNG KẾT NỐI MẠNG ĐỂ LÀM GIAO DIỆN (UI MODE)
        // ==========================================================
        /*
        new Thread(() -> {
            try {
                AuctionClientSocket.getInstance().connectToServer("localhost", 5000);
            } catch (Exception e) {
                System.out.println("Không thể kết nối đến Server!");
            }
        }).start();
        */

        // ==========================================================
        // 2. CHỌN MÀN HÌNH MÀ ÔNG ĐANG MUỐN THIẾT KẾ VÀ TEST UI
        // ==========================================================
        // Bật màn hình Login:
        // SceneManager.getInstance().switchScene("/com/example/auction/client/view/login-view.fxml", "Đăng nhập - Team 15");

        // HOẶC test thẳng màn hình Thêm Sản Phẩm (Bỏ comment dòng này, comment dòng trên):
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/MainAfterLogin.fxml", "");

        // HOẶC test thẳng màn hình Chi tiết sản phẩm:
        // SceneManager.getInstance().switchScene("/com/example/auction/client/view/detail-view.fxml", "Chi tiết SP - Test UI");

        // ==========================================================
        // 3. TẠM TẮT ĐÓNG SOCKET KHI TẮT APP
        // ==========================================================
        stage.setOnCloseRequest(e -> {
            // AuctionClientSocket.getInstance().closeConnection();
            System.exit(0);
        });
        stage.setMaximized(true);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}