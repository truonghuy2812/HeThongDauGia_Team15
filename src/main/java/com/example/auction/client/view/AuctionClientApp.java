package com.example.auction.client.view;
import com.example.auction.client.clientnetworkmanager.AuctionClientSocket;
import com.example.auction.client.controller.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXML;

import java.io.IOException;
public class AuctionClientApp extends Application {
    @Override
    public void start(Stage stage) {
        // Khởi tạo chuyển cảnh
        SceneManager.getInstance().setPrimaryStage(stage);
        // Mở kết nối Socket
        try {
            AuctionClientSocket.getInstance().connectToServer("localhost", 5000);
        } catch (Exception e) {
            System.out.println("Không thể kết nối đến Server!");
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AuctionClientApp.class.getResource("/com/example/auction/client/view/Main.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
            stage.setTitle("Phần mềm Đấu giá - Team 15");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            System.out.println("Lỗi không tìm thấy file giao diện FXML!");
            e.printStackTrace(); // Lệnh này giúp in chi tiết vạch đỏ ra Console để dễ bắt bệnh
        }
        // Tắt app thì ngắt mạng
        stage.setOnCloseRequest(e -> {
            AuctionClientSocket.getInstance().closeConnection();
            System.exit(0);
        });
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}