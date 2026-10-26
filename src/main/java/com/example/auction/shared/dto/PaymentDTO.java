package com.example.auction.shared.dto;

import java.io.Serializable;
import java.time.Instant;

public record PaymentDTO(
        Long ownerId,
        Double amount,
        String status,
        Instant time, Long id, String message) implements Serializable {
    private final static long serialVersionId = 1L;
}
