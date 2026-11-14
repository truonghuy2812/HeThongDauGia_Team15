package com.example.auction.client.view;
import atlantafx.base.theme.PrimerLight;
import com.example.auction.client.clientnetworkmanager.AuctionClientSocket;
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
        // 1. MỞ LUỒNG KẾT NỐI MẠNG ĐẾN SERVER (ONLINE MODE)
        // ==========================================================
        new Thread(() -> {
            try {
                com.example.auction.client.clientnetworkmanager.AuctionClientSocket.getInstance().connectToServer("localhost", 5000);
                System.out.println("Đã gửi yêu cầu kết nối đến Server thành công!");
            } catch (Exception e) {
                System.err.println("Không thể kết nối đến Server. Vui lòng kiểm tra lại AuctionServer đã chạy chưa!");
            }
        }).start();

        // ==========================================================
        // 2. KHỞI CHẠY MÀN HÌNH ĐẦU TIÊN (ĐĂNG NHẬP)
        // ==========================================================
        // Khi chạy online, bắt buộc phải vào màn Đăng nhập/Đăng ký trước
        com.example.auction.client.controller.SceneManager.getInstance().switchScene("/com/example/auction/client/view/login-view.fxml", "Đăng nhập - Team 15");

        // Các màn hình test UI cũ tạm thời được comment lại
        // SceneManager.getInstance().switchScene("/com/example/auction/client/view/MainAfterLogin.fxml", "");
        // SceneManager.getInstance().switchScene("/com/example/auction/client/view/detail-view.fxml", "Chi tiết SP - Test UI");

        // ==========================================================
        // 3. ĐÓNG SOCKET AN TOÀN KHI TẮT APP
        // ==========================================================
        stage.setOnCloseRequest(e -> {
            // Ngắt kết nối để Server không bị treo tài nguyên
            com.example.auction.client.clientnetworkmanager.AuctionClientSocket.getInstance().closeConnection();
            System.exit(0);
        });

        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}