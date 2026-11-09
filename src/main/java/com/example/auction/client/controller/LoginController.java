package com.example.auction.client.controller;

import com.example.auction.client.clientnetworkmanager.AuctionClientSocket;
import com.example.auction.client.clientnetworkmanager.MessageHandler;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.SVGPath;

import java.io.ObjectOutputStream;

public class LoginController implements MessageHandler {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private SVGPath eyeIcon;

    @FXML
    public void initialize() {
        // Đăng ký để lắng nghe Server trả lời
        AuctionClientSocket.getInstance().setMessageHandler(this);
        SceneManager.getInstance().setupPasswordToggle(txtPassword, eyeIcon);
    }

    @FXML
    private void handleLogin() {
        String inputAccount = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (inputAccount.isEmpty() || password.isEmpty()) {
            SceneManager.getInstance().showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
            return;
        }
        System.out.println("Đang gửi yêu cầu đăng nhập cho: " + inputAccount);

        // ==============================================================
        // CODE GỌI MẠNG THẬT: Gửi dữ liệu xuống Server để tra cứu SQL
        // ==============================================================
        Thread thread = new Thread(() -> {
            try {
                synchronized (AuctionClientSocket.getInstance()) {
                    ObjectOutputStream out = AuctionClientSocket.getInstance().getOut();
                    // Đóng gói DTO gửi đi. Backend đang dùng email để check login
                    // Sửa lại dòng này trong handleLogin() của LoginController.java:
                    // Đổi lại vị trí: tham số thứ 3 là password, tham số thứ 4 là inputAccount (email)
                    UserDTO userDTO = new UserDTO(null, null, password, inputAccount, null, null);
                    MessageProtocol request = new MessageProtocol("LOGIN", userDTO, null, null);
                    out.writeObject(request);
                    out.flush();
                }
            } catch (Exception e) {
                Platform.runLater(() ->
                        SceneManager.getInstance().showAlert(Alert.AlertType.ERROR, "Lỗi mạng", "Không thể kết nối đến Server!")
                );
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void onMessageReceived(MessageProtocol response) {
        // Bắt buộc bọc trong Platform.runLater vì đang thao tác giao diện từ luồng mạng
        Platform.runLater(() -> {
            if ("SUCCESS".equals(response.status())) {
                UserDTO user = (UserDTO) response.data();

                // Lưu session (Ưu tiên lấy username, nếu không có thì lấy email)
                SessionManager.login(user.username() != null ? user.username() : user.email());

                // Chuyển màn hình dựa theo Role thực tế lấy từ Database
                if ("ADMIN".equals(user.role())) {
                    SceneManager.getInstance().switchScene("/com/example/auction/client/view/AdminController.fxml", "Màn Duyệt đấu giá");
                } else {
                    SceneManager.getInstance().switchScene("/com/example/auction/client/view/MainAfterLogin.fxml", "Trang chủ hệ thống");
                }
            } else if ("ERROR".equals(response.status())) {
                SceneManager.getInstance().showAlert(Alert.AlertType.ERROR, "Thất bại", response.message());
                txtPassword.clear();
            }
        });
    }

    @FXML
    private void goToRegister() {
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/signup.fxml", "Đăng ký tài khoản - Team 15");
    }
}