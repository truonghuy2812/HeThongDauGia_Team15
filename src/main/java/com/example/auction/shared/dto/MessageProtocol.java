package com.example.auction.shared.dto;

import java.io.Serializable;

/**
 * MessageProtocol - WRAPPER CHO MESSAGES
 * Để làm gì:
 * Vấn đề: Server không biết message từ client là gì
 * - Client gửi UserDTO → Server không biết là LOGIN hay SIGNUP
 * - Phải guess type → error prone
 * <p>
 * Giải pháp: Wrapper với "type" field
 * - new MessageProtocol("LOGIN", userDTO, null, null)
 * - Server biết ngay: type = "LOGIN"
 * - Route tới LoginHandler
 * Vì sao cần:
 * 1. Unified format:
 * - Request: MessageProtocol("LOGIN", userDTO, null, null)
 * - Response: MessageProtocol("LOGIN", userData, "SUCCESS", "OK")
 * - Error: MessageProtocol("LOGIN", null, "ERROR", "Sai email")
 * → Tất cả cùng format → dễ handle
 * <p>
 * 2. Routing:
 * switch (msg.type()) {
 * case "LOGIN" → LoginHandler
 * case "BID" → BidHandler
 * }
 * → Không cần instanceof check
 * 3. Scale:
 * - Thêm message type mới? Chỉ thêm case trong switch
 * - Không phải thay đổi protocol
 * <p>
 * 4. Debug:
 * - Biết message type → dễ debug
 * - Log: " Message from user1: LOGIN"
 * Thiết kế:
 * Record: Tại sao không class?
 * - Record = immutable data class (Java 16+)
 * - Tự generate: constructor, getter, toString, equals, hashCode
 * - Lightweight → perfect cho data transfer
 * - Implement Serializable → gửi qua socket
 */
public record MessageProtocol(
  String type,

  Object data,

  String status,

  String message

) implements Serializable {

  private static final long serialVersionUID = 1L;
}
