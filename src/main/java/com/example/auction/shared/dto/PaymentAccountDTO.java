package com.example.auction.shared.dto;

import java.io.Serializable;
import java.time.Instant;

public record PaymentAccountDTO(
    Long ownerId,
    Double balance,
    Double frozenBalance,
    Instant time) implements Serializable {
  private final static long serialVersionId = 1L;
} 
