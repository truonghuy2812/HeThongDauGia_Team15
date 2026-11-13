package com.example.auction.server.servernetworkmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// ClientSession - ĐẠI DIỆN CHO 1 CLIENT CONNECTION

import com.example.auction.server.handlers.HandlerFactory;
import com.example.auction.server.service.ClientService;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;
import com.example.auction.server.entity.Role;

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
 *
 * Giải pháp: ClientSession
 * - Mỗi client → 1 ClientSession instance
 * - Implement Runnable → chạy trên thread
 * - run() = vòng lặp: đọc messages → xử lý → gửi response
 * - Track state: currentUser, connected, socket, streams
 *
 * Ví dụ flow:
 * 1. Server accept client → new ClientSession(socket)
 * 2. executor.submit(session) → Chạy trên thread
 * 3. session.run():
 *    ├─ while (connected):
 *    │  ├─ readObject() → chặp chờ message
 *    │  ├─ handleMessage() → process
 *    │  └─ Gửi response
 *    ├─ EOFException → client close
 *    └─ disconnect() → cleanup
 * STREAM INITIALIZATION ORDER: OUTPUT TRƯỚC INPUT!
 *
 * ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
 * ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
 * như này sẽ bị deadlock
 *
 * Vấn đề: Deadlock!
 * - ObjectInputStream constructor chờ stream header từ output stream
 * - Nếu output stream chưa tạo → chờ vô hạn
 *
 * ĐÚNG:
 * ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
 * out.flush();  ← Gửi stream header
 * ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
 *
 * Output trước → tạo stream header
 * Input sau → nhận stream header → không deadlock
 * */
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
                // khi client gửi -> gửi về
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
     * handleMessage - Convert UserDTO → UserSession
     *
     * CHANGE:
     * 1. Receive MessageProtocol
     * 2. Cast data → UserDTO (from network)
     * 3. If type LOGIN/SIGNUP → convert UserDTO → UserSession
     * 4. Pass UserSession tới handler
     */

    private void handleMessage(MessageProtocol msg) {
        try {
            // get data từ message
            Object data =  msg.data();

            // Convert UserDTO → UserSession (nếu là login/signup)
            if ("LOGIN".equals(msg.type()) || "SIGNUP".equals(msg.type())) {
                UserDTO userDTO = (UserDTO) data;

                // Nếu Client gửi role là null hoặc rỗng thì mặc định là Role.GUEST
                Role role = (userDTO.role() != null && !userDTO.role().isBlank())
                        ? Role.valueOf(userDTO.role().toUpperCase())
                        : Role.GUEST;

                UserSession tempSession = new UserSession(
                        1L,              // Placeholder, sẽ update từ DB
                        userDTO.username(),
                        userDTO.email(),
                        role,
                        null
                );
                var handler = HandlerFactory.getHandler(msg.type());
                handler.handle(this, msg, tempSession);
            }
            else {
                // Handlers khác (BID, CREATE_AUCTION, ...)
                var handler = HandlerFactory.getHandler(msg.type());
                handler.handle(this, msg, currentUserSession);
            }
        }
        catch (IllegalArgumentException e) {
            // Handler kh tồn tại
            sendMessage(new MessageProtocol(
                    msg.type(),
                    null,
                    "ERROR",
                    "Message type kh hỗ trợ: " + msg.type()
            ));
        }
        catch (Exception e) {
            sendMessage(new MessageProtocol(
                    msg.type(),
                    null,
                    "ERROR",
                    "Lỗi xử lý: " + e.getMessage()
            ));
        }
    }

    /**
     * sendMessage - Gửi message tới client
     *
     * Input: MessageProtocol message
     *
     * Synchronized:
     * - Nhiều threads có thể gọi sendMessage() cùng lúc
     * - ObjectOutputStream không thread-safe
     * - synchronized block → chỉ 1 thread ghi vào stream lúc một
     *
     * Scenario mà cần synchronized:
     * - BidHandler.handle() gọi: sendMessage(bidResponse)
     * - Cùng lúc, ClientService.broadcast() gọi: session.sendMessage(update)
     * - Cả hai cố ghi vào ObjectOutputStream
     * - Nếu không synchronized → dữ liệu corrupt!
     *
     * Với synchronized:
     * - BidHandler vào synchronized block → ghi → exit
     * - Broadcast chờ, vào synchronized block → ghi → exit
     * - Dữ liệu an toàn
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
     *
     * Gọi từ: LoginHandler.handle()
     * Ví dụ: setCurrentUser(userDTO)
     *
     * Mục đích:
     * - Track user của session này
     * - Validation: Bidder phải là currentUser
     * - ClientService.removeClient() sử dụng currentUser
     *
     * Vận dụng:
     * - LoginHandler:
     *   if (validate login OK):
     *     session.setCurrentUser(userDTO)
     *     ClientService.addClient(userDTO.id(), this)
     */
    public void setCurrentUserSession(UserSession userSession) {
        this.currentUserSession = userSession;
    }

    public UserSession getCurrentUserSession() {
        return currentUserSession;
    }

    /**
     * disconnect - Đóng connection
     *
     * Gọi khi:
     * - run() exit (EOFException, IOException)
     * - User logout
     * - Timeout (not implemented yet)
     *
     * Cleanup:
     * 1. Đóng streams
     * 2. Đóng socket
     * 3. Remove từ ClientService
     *
     * Vận dụng:
     * - run() finally block
     *   finally { disconnect(); }
     * - User logout request
     *   handler gọi: session.disconnect()
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
            // ClientService sẽ tự động broadcast "user offline"
            if (currentUserSession != null) {
                ClientService.getInstance().removeClient(currentUserSession.getId()); // sau cần phải sửa sang Long vì đang để String
            }
        }
        catch (IOException e) {
            log.error("Exception", e);
        }
    }

    /**
     * isConnected - Kiểm tra connection còn active
     *
     * Vận dụng:
     * - ClientService.sendTo(): if (session.isConnected()) { sendMessage(); }
     * - ClientService.broadcast(): if (session.isConnected()) { sendMessage(); }
     */
    public boolean isConnected() {
        return connected;
    }

    public Socket getSocket() {
        return socket;
    }
}
