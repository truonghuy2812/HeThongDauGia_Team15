package com.example.auction.server;

import com.example.auction.server.servernetworkmanager.ClientSession;
import com.example.auction.server.service.ClientService;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class AuctionServer {
    public static void main(String[] args) {
        final int PORT = 5000;
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Server đã mở tại port: " + PORT);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Có người kết nối: " + clientSocket.getInetAddress());
                    ClientSession session = new ClientSession(clientSocket);
                    ClientService.getInstance().addClient(session);
                    executor.submit(session);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}