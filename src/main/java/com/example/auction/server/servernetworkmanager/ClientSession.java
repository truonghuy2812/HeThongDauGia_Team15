package com.example.auction.server.servernetworkmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ĐÃ COMMENT CÁC IMPORT BỊ LỖI DO ĐÃ XÓA FILE
// import com.example.auction.server.handlers.HandlerFactory;
// import com.example.auction.server.entity.Role;

import com.example.auction.server.service.ClientService;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Vấn đề:
 * - 100 clients online = 100 connections
 * - Server phải track state của mỗi client
 * - Mỗi client gửi multiple messages (không phải 1 request)
 * - ObjectInputStream/ObjectOutputStream cần setup phức tạp
 */
public class ClientSession implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ClientSession.class);

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private UserSession currentUserSession; // user hiện tại (null nếu ch login)
    private boolean connected = true;

    public ClientSession(Socket socket) {
        this.socket = socket;

        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.out.flush();  // gửi header

            // in sau, nh header
            this.in = new ObjectInputStream(socket.getInputStream());

            log.info("ClientSession created: " + socket.getInetAddress());
        }
        catch (IOException e) {
            log.error("Exception", e);
            connected = false;
        }
    }

    @Override
    public void run() {
        try {
            // chặp chờ message từ client
            while (connected) {
                // readObject() -> chặp chờ message
                MessageProtocol msg = (MessageProtocol) in.readObject();

                String user = currentUserSession != null ? currentUserSession.getUserName() : "GUEST";
                log.info("Message from " + user + ": " + msg.type());

                // xử lý message
                handleMessage(msg);
            }
        }
        catch (EOFException e) {
            // client close connection -> EOF
            log.info("Client disconnected");
        }
        catch (IOException | ClassNotFoundException e) {
            log.warn("Error: " + e.getMessage());
        }
        finally {
            // cleanup: đóng connection, remove từ ClientService
            disconnect();
        }
    }

    /**
     * handleMessage - Nơi phân luồng xử lý gói tin
     */
    private void handleMessage(MessageProtocol msg) {
        // TODO: Nhóm 15 tự code logic gọi Handler tương ứng với msg.type() tại đây
        System.out.println("Đã nhận tin nhắn loại: " + msg.type());
    }

    /**
     * sendMessage - Gửi message tới client
     */
    public synchronized void sendMessage(MessageProtocol message) {
        try {
            out.writeObject(message);
            out.flush();
            log.info("Sent: " + message.type());
        }
        catch (IOException e) {
            log.warn("Failed to send: " + e.getMessage());
            connected = false;
        }
    }

    /**
     * setCurrentUser - Set user khi login
     */
    public void setCurrentUserSession(UserSession userSession) {
        this.currentUserSession = userSession;
    }

    public UserSession getCurrentUserSession() {
        return currentUserSession;
    }

    /**
     * disconnect - Đóng connection
     */
    public void disconnect() {
        if (!connected) return; // đã disconnect r

        connected = false;

        try {
            if (out != null ) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }

            log.info("🔌 Connection closed: " + socket.getInetAddress());

            // Nếu user đã login, remove khỏi online list
            if (currentUserSession != null) {
                ClientService.getInstance().removeClient(currentUserSession.getId()); 
            }
        }
        catch (IOException e) {
            log.error("Exception", e);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public Socket getSocket() {
        return socket;
    }
}