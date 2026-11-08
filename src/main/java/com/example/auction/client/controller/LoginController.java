package com.example.auction.client.controller;

import com.example.auction.client.clientnetworkmanager.AuctionClientSocket;
import com.example.auction.client.clientnetworkmanager.MessageHandler;
import com.example.auction.server.entity.User;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.SVGPath;

import java.io.ObjectOutputStream;

// 1. THÊM implements MessageHandler VÀO ĐÂY
public class LoginController implements MessageHandler {

    @FXML private TextField txtUsername; // Nếu Backend chốt là Email thì tự đổi tên/promptText cho hợp
    @FXML private PasswordField txtPassword;
    @FXML private SVGPath eyeIcon;

    @FXML
    public void initialize() {
        // 2. BẮT BUỘC: Đăng ký để lắng nghe Server trả lời
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
        // 1. TẠM ẨN ĐOẠN GỬI MẠNG THẬT NÀY ĐI (VÌ CHƯA CÓ SERVER)
        // ==============================================================
    /*
    Thread thread = new Thread(() -> {
        try {
            synchronized (AuctionClientSocket.getInstance()) {
                ObjectOutputStream out = AuctionClientSocket.getInstance().getOut();
                UserDTO userDTO = new UserDTO(1L, "", password, inputAccount, "GUEST", null);
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
    */
        // ==============================================================
        // 2. CHỈ ĐỂ MỞ DÒNG NÀY ĐỂ GIẢ LẬP SERVER TRẢ VỀ SUCCESS
        // ==============================================================
// ==============================================================
        // 2. MOCK DATA (GIẢ LẬP SERVER) DỰA THEO INPUT CỦA ÔNG
        // ==============================================================
        if ("admin".equals(inputAccount)) {
            // Giả lập Server trả về tài khoản ADMIN
            onMessageReceived(new MessageProtocol("LOGIN",
                    new UserDTO(1L, "admin", "123", "admin@gmail.com", "ADMIN", null),
                    "SUCCESS", "Đăng nhập OK"));
        }
        else if ("guest".equals(inputAccount)) {
            // Giả lập Server trả về tài khoản GUEST
            onMessageReceived(new MessageProtocol("LOGIN",
                    new UserDTO(2L, "guest", "123", "guest@gmail.com", "GUEST", null),
                    "SUCCESS", "Đăng nhập OK"));
        }
        else {
            // Giả lập Server báo lỗi sai mật khẩu
            onMessageReceived(new MessageProtocol("LOGIN", null, "ERROR", "Tài khoản hoặc mật khẩu không đúng!"));
        }    }

    // 4. HÀM HỨNG DATA TỪ SERVER - ÔNG TOÀN QUYỀN QUYẾT ĐỊNH CHUYỂN CẢNH Ở ĐÂY
    @Override
    public void onMessageReceived(MessageProtocol response) {
        // Bắt buộc bọc trong Platform.runLater vì đang thao tác giao diện từ luồng mạng
        Platform.runLater(() -> {
            if ("SUCCESS".equals(response.status())) {
                UserDTO user = (UserDTO) response.data();

                // Lưu session
                SessionManager.login(user.username() != null ? user.username() : user.email());

                // Ông tự gọi file FXML của ông, đéo liên quan đến thằng Backend nữa
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

    @FXML
    private void goToRegister() {
        SceneManager.getInstance().switchScene("/com/example/auction/client/view/signup.fxml", "Đăng ký tài khoản - Team 15");
    }
}