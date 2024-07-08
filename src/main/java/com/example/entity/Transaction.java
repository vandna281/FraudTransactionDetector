package com.example.entity;

import jakarta.persistence.*;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "source_account", nullable = false)
    private String sourceAccount;

    @Column(name = "destination_account", nullable = false)
    private String destinationAccount;

    @Column(name = "source_country", nullable = false)
    private String sourceCountry;

    @Column(name = "destination_country", nullable = false)
    private String destinationCountry;

    private Boolean isFraudulent;
    private Double amount;
    private String timestamp;

    public Transaction(double amount, String sourceCountry, String destinationCountry) {
        this.amount = amount;
        this.sourceCountry = sourceCountry;
        this.destinationCountry = destinationCountry;
    }

    public Transaction() { }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceCountry() {
        return sourceCountry;
    }

    public void setSourceCountry(String sourceCountry) {
        this.sourceCountry = sourceCountry;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public String getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(String sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(String destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getFraudulent() {
        return isFraudulent;
    }

    public void setFraudulent(Boolean fraudulent) {
        isFraudulent = fraudulent;
    }
}

