module com.auction {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;
    requires cloudinary.core;
    requires jbcrypt;
    requires org.slf4j;

    opens com.example.auction.client.view to javafx.graphics, javafx.fxml;

    exports com.example.auction.client.view;
    exports com.example.auction.shared.dto;
    exports com.example.auction.server;
    exports com.example.auction.client.controller;
    opens com.example.auction.client.controller to javafx.fxml, javafx.graphics, org.testfx, org.junit.jupiter.api;
    opens com.example.auction.client.clientnetworkmanager to javafx.graphics, org.testfx, org.junit.jupiter.api;
}