package com.example.auction.client.clientnetworkmanager;

import com.example.auction.shared.dto.MessageProtocol;

public interface MessageHandler {
    void onMessageReceived(MessageProtocol messageProtocol);
}
