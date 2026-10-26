package com.example.auction.shared.dto;

import com.example.auction.server.entity.PaymentAccount;

import java.io.Serializable;

public record UserDTO(
  Long id,
  String username,
  String password,
  String email,
  String role,
  PaymentAccountDTO account
) implements Serializable {
  private static final long serialVersionUID = 1L;


  public UserDTO(Long id, String username, String password, String email, String role, PaymentAccountDTO account) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.email = email;
    this.role = role;
    this.account = account;
  }
}
