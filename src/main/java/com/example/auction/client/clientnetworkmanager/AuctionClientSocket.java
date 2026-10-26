package com.example.auction.client.clientnetworkmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.auction.server.entity.User;
import com.example.auction.shared.dto.MessageProtocol;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

/**
 * AppContext - Singleton quản lý tài nguyên toàn app
 * - 1 Socket duy nhất
 * - 1 User session
 * - Thread-safe
 */

public class AuctionClientSocket {
    private static final Logger log = LoggerFactory.getLogger(AuctionClientSocket.class);


    private static AuctionClientSocket instance;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private User currentUser;
    private MessageProtocol currentMsg;

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    private MessageHandler messageHandler;

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    private AuctionClientSocket() {
    }

    public static AuctionClientSocket getInstance() {
        if (instance == null) {
            synchronized (AuctionClientSocket.class) {
                if (instance == null) {
                    instance = new AuctionClientSocket();
                }
            }
        }
        return instance;
    }

    // SOCKET
    public void connectToServer(String host, int port) throws IOException {
        if (socket == null || socket.isClosed()) {
            try {
                this.socket = new Socket(host, port);
                this.out = new ObjectOutputStream(socket.getOutputStream());
                this.out.flush();
                log.info("Connected to server: " + host + ":" + port);
                this.in = new ObjectInputStream(socket.getInputStream());
                Thread listenThread = new Thread(() -> {
                    try {
                        while (socket.isConnected()) {
                            MessageProtocol receivedMsg = (MessageProtocol) in.readObject();
                            currentMsg = receivedMsg;
                            log.debug("MSG HIEN TAI " + currentMsg);
                            Platform.runLater(() -> {
                                if (messageHandler != null) {
                                    messageHandler.onMessageReceived(receivedMsg);
                                }
                            });
                        }
                    } catch (SocketException e) {
                        Platform.runLater(() -> {
                            try {
                                javafx.stage.Stage stage = com.example.auction.client.controller.SceneManager.getInstance().getPrimaryStage();
                                if (stage != null) {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/auction/client/view/waitting.fxml"));
                                    stage.setScene(new javafx.scene.Scene(loader.load()));
                                    stage.show();

                                }
                            } catch (IOException ex) {
                                log.error("Exception", ex);
                            }
                        });
                    } catch (IOException e) {
                        log.info("ĐÃ XẢY RA LỖI TRUYỀN TẢI DỮ LIỆU");
                        closeConnection();
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
                listenThread.start();
            } catch (ConnectException e) {
                log.info("Server not found");
                throw new SocketException("Server not found");
            } catch (SocketException e) {
                throw new SocketException("Server not found");
            }

        }
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public MessageProtocol getIn() throws IOException, ClassNotFoundException {
        return currentMsg;
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    public void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            log.info("Disconnected from server");
        } catch (Exception e) {
            log.error("Exception", e);
        }
    }

    // USER SESSION
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Chỉ xóa user trên client (không báo server).
     */
    public void logout() {
        currentUser = null;
    }

    public synchronized void sendMessage(MessageProtocol msg){
        try {
            this.out.writeObject(msg);
            this.out.flush();
        } catch (IOException e) {
            log.error("Exception", e);
        }
    }

    /**
     * Đăng xuất đầy đủ: gửi LOGOUT lên server rồi xóa session client.
     * Dùng chung cho Main2, Admin, v.v.
     */
    public void performLogout() {
        if (!isLoggedIn()) {
            return;
        }
        Long userId = getCurrentUserId();
        try {
            synchronized (this) {
                if (out != null && userId != null) {
                    out.writeObject(new MessageProtocol("LOGOUT", userId, null, null));
                    out.flush();
                }
            }
        } catch (IOException e) {
            log.error("Lỗi gửi LOGOUT: " + e.getMessage());
            log.error("Exception", e);
        } finally {
            currentUser = null;
        }
    }

    public String getCurrentUsername() {
        return isLoggedIn() ? currentUser.getUsername() : null;
    }

    public Long getCurrentUserId() {
        return isLoggedIn() ? currentUser.getId() : null;
    }

}
