package com.example.auction.client.view;
import atlantafx.base.theme.PrimerLight; // hoặc CupertinoLight, NordLight tùy thích
import javafx.application.Application;


import com.example.auction.client.clientnetworkmanager.AuctionClientSocket;
import com.example.auction.client.controller.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class AuctionClientApp extends Application {
    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        SceneManager.getInstance().setPrimaryStage(stage);
        new Thread(() -> {
            try {
                AuctionClientSocket.getInstance().connectToServer("localhost", 5000);
            } catch (Exception e) {
                System.out.println("Không thể kết nối đến Server!");
            }
        }).start();
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/MainAfterLogin.fxml", "Đăng nhập - Team 15");
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