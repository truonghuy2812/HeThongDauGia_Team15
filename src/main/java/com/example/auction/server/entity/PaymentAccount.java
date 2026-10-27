package com.example.auction.server.entity;
import java.io.Serializable;
public class PaymentAccount implements Serializable{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String accountNumber;
    private double balance;

    public PaymentAccount() {}

    public PaymentAccount(Long id, Long userId, String accountNumber, double balance) {
        this.id = id;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
