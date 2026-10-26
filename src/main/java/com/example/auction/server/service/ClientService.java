package com.example.auction.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ĐÃ COMMENT IMPORT UserDAO VÌ ĐÃ XÓA DAO LOGIC
// import com.example.auction.database.dao.UserDAO;

import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.servernetworkmanager.UserSession;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClientService - CORE CỦA REAL-TIME SYSTEM
 */
public class ClientService {
    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    private static ClientService instance;

    private Map<Long, ClientSession> onlineClients = new ConcurrentHashMap<>();
    private Map<ClientSession, Long> currentSession = new ConcurrentHashMap<>();

    private ClientService() {};

    public static ClientService getInstance() {
        if (instance == null) {
            synchronized (ClientService.class) {
                if (instance == null) {
                    instance = new ClientService();
                }
            }
        }
        return instance;
    }
    
    Long guestId = 1000000L;

    public void addClient(ClientSession session) {
        onlineClients.forEach((key, value) -> log.info(key + " : " + value));
        Long userId = guestId++;
        onlineClients.put(userId, session);
        currentSession.put(session, userId);
        log.info(userId + " online (Total: " + onlineClients.size() + ")");

        MessageProtocol notifyMsg = new MessageProtocol(
            "USER_ONLINE",
            null,
            "INFO",
            userId + " vừa online"
        );
        broadcastExcept(userId, notifyMsg);
    }

    public void signUpToMember(Long userId, ClientSession session) {
        Long guestId = currentSession.get(session);
        if (guestId != null) {
            onlineClients.remove(guestId);
        }
        onlineClients.put(userId, session);
        currentSession.put(session, userId);
        log.info(userId + " online (Total: " + onlineClients.size() + ")");
    }

    public void logoutMember(ClientSession session, Long memberUserId) {
        if (memberUserId != null) {
            onlineClients.remove(memberUserId);
            MessageProtocol notifyMsg = new MessageProtocol(
                "USER_OFFLINE",
                null,
                "INFO",
                memberUserId + " vừa offline"
            );
            broadcastExcept(memberUserId, notifyMsg);
        }
        Long newGuestId = guestId++;
        onlineClients.put(newGuestId, session);
        currentSession.put(session, newGuestId);
        log.info("Logout: member " + memberUserId + " -> guest " + newGuestId);
    }

    /** Gửi message tới mọi admin đang online (theo role trong DB). */
    public void sendToAllAdmins(MessageProtocol message) {
        // TODO: Gọi UserDAO lấy danh sách ID của Admin và gửi thông báo
        // for (Long adminId : UserDAO.getInstance().getAdminUserIds()) {
        //     sendTo(adminId, message);
        // }
    }
    
    public void removeClient(Long userId) {
        onlineClients.remove(userId);
        log.info(userId + " offline (Total: " + onlineClients.size() + ")");
        MessageProtocol notifyMsg = new MessageProtocol(
            "USER_OFFLINE",
            null,
            "INFO",
            userId + " vừa offline"
        );
        broadcast(notifyMsg);
    }

    public void sendTo(Long userId, MessageProtocol message) {
        ClientSession session = onlineClients.get(userId);
        if (session != null && session.isConnected()) {
            session.sendMessage(message);
            log.info("Sent to " + userId + ": " + message.type());
        } else {
            log.info("User " + userId + " not online or disconnected");
        }
    }

    public void broadcast(MessageProtocol message) {
        log.info("BROADCAST: " + message.type() +
            " → " + onlineClients.size() + " clients");

        for (ClientSession session : onlineClients.values()) {
            if (session.isConnected()) {
                session.sendMessage(message);
            }
        }
    }

    public void broadcastExcept(Long exceptUserId, MessageProtocol message) {
        log.info("BROADCAST (except " + exceptUserId + "): " + message.type());

        for (Map.Entry<Long, ClientSession> entry : onlineClients.entrySet()) {
            Long clientId = entry.getKey();
            ClientSession session = entry.getValue();
            if (!clientId.equals(exceptUserId) && session.isConnected()) {
                session.sendMessage(message);
            }
        }
    }

    public List<UserDTO> getOnlineUsers() {
        List<UserDTO> users = new ArrayList<>();
        // TODO: Nhóm 15 tự code lại hàm getOnlineUsers() dựa theo UserDTO
        // Hiện tại bị lỗi đỏ vì getPaymentAccount() đã bị xóa hoặc thay đổi
        
        // for (ClientSession session : onlineClients.values()) {
        //     UserSession userSession = session.getCurrentUserSession();
        //     if (userSession != null) {
        //         UserDTO userDTO = new UserDTO(
        //             userSession.getId(),
        //             userSession.getUserName(),
        //             null,
        //             userSession.getEmail(),
        //             userSession.getRole().name(),
        //             null // userSession.getPaymentAccount().convert()
        //         );
        //         users.add(userDTO);
        //     }
        // }
        return users;
    }
    
    public ClientSession getClientSessionById(long id){
        return onlineClients.get(id);
    }

    public boolean isOnline(Long userId) {
        ClientSession session = onlineClients.get(userId);
        return session != null && session.isConnected();
    }

    public int getOnlineCount() {
        return onlineClients.size();
    }
}