package com.example.auction.server.entity;

import java.time.Instant;

public class Entity {

    protected int id;
    protected Instant createdAt;
    protected Instant updatedAt;

    // cnstructor mặc định
    public Entity() {
    }

    //constructor có tham số (super(...) bên file Auction)
    public Entity(int id, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- GETTERS & SETTERS ---

    public int getId() {
        return this.id;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}