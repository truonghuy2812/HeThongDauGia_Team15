package com.example.auction.client.view;

import com.example.auction.client.clientnetworkmanager.AuctionClientSocket;
import com.example.auction.client.controller.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class AuctionClientApp extends Application {
    @Override
    public void start(Stage stage) {
        SceneManager.getInstance().setPrimaryStage(stage);
        new Thread(() -> {
            try {
                AuctionClientSocket.getInstance().connectToServer("localhost", 5000);
            } catch (Exception e) {
                System.out.println("Không thể kết nối đến Server!");
            }
        }).start();
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/Main.fxml", "Đăng nhập - Team 15");
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