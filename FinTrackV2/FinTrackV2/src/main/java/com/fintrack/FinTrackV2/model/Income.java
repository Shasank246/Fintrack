package com.fintrack.FinTrackV2.model;

import jakarta.persistence.*;

@Entity

public class Income {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String source;

    private Double amount;

    @ManyToOne

    private User user;

    // GETTERS

    public Long getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public Double getAmount() {
        return amount;
    }

    public User getUser() {
        return user;
    }

    // SETTERS

    public void setId(Long id) {
        this.id = id;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setUser(User user) {
        this.user = user;
    }
}