package com.example.auction.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.application.Platform;

import com.example.auction.client.clientnetworkmanager.AuctionClientSocket;
import com.example.auction.client.clientnetworkmanager.MessageHandler;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;
import java.io.ObjectOutputStream;

public class SignupController implements MessageHandler {

    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;

    @FXML
    public void initialize() {
        AuctionClientSocket.getInstance().setMessageHandler(this);
    }

    @FXML
    private void handleRegister() {
        String username = txtUsername.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (username.trim().isEmpty() || email.trim().isEmpty() ||
                password.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            SceneManager.getInstance().showAlert(Alert.AlertType.ERROR, "Lỗi mật khẩu", "Mật khẩu nhập lại không khớp!");
            return;
        }

        System.out.println("Đang gửi yêu cầu đăng ký cho: " + username + " - Email: " + email);

        Thread thread = new Thread(() -> {
            try {
                synchronized (AuctionClientSocket.getInstance()) {
                    ObjectOutputStream out = AuctionClientSocket.getInstance().getOut();

                    UserDTO userDTO = new UserDTO(null, username, password, email, "MEMBER", null);
                    MessageProtocol request = new MessageProtocol("SIGNUP", userDTO, null, null);

                    out.reset();
                    out.writeObject(request);
                    out.flush();
                }
            } catch (Exception e) {
                Platform.runLater(() ->
                        SceneManager.getInstance().showAlert(Alert.AlertType.ERROR, "Lỗi mạng", "Không kết nối được Server!")
                );
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void onMessageReceived(MessageProtocol response) {
        Platform.runLater(() -> {
            if ("SUCCESS".equals(response.status())) {
                SceneManager.getInstance().showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đăng ký tài khoản thành công!");
                goToLogin();
            } else if ("ERROR".equals(response.status())) {
                SceneManager.getInstance().showAlert(Alert.AlertType.ERROR, "Thất bại", response.message());
            }
        });
    }

    @FXML
    private void goToLogin() {
        System.out.println("Chuyển hướng về màn hình Đăng nhập...");
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/login-view.fxml", "Đăng nhập - Team 15");
    }
}