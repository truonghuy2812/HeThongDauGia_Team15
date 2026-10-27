package com.example.auction.server.entity;

import java.io.IOException;
import java.net.Socket;


    public class AuctionClientSocket {
        private static AuctionClientSocket instance;
        private Socket socket;

        private AuctionClientSocket() {}

        public static synchronized AuctionClientSocket getInstance() {
            if (instance == null) {
                instance = new AuctionClientSocket();
            }
            return instance;
        }

        public void connectToServer(String host, int port) {
            try {
                this.socket = new Socket(host, port);
                System.out.println("✅ Đã kết nối tới Server: " + host + ":" + port);
            } catch (IOException e) {
                System.err.println("❌ Không thể kết nối tới Server: " + e.getMessage());
            }
        }

        public Socket getSocket() {
            return socket;
        }
    }

