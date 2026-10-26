package com.example.auction.client.view;

import com.example.auction.client.clientnetworkmanager.AuctionClientSocket;
import com.example.auction.client.controller.SceneManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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

        // Tạo một màn hình trống rỗng cho Team 15 tự code
        Scene scene = new Scene(new AnchorPane(), 900, 600);
        stage.setTitle("Phần mềm Đấu giá - Team 15");
        stage.setScene(scene);
        stage.centerOnScreen();
        
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