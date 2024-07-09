package com.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "transactionvalidationrule")
public class TransactionValidation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "transaction_limit")
    private Double transactionLimit;

    public TransactionValidation(Long id, Double transactionLimit) {
        this.id = id;
        this.transactionLimit = transactionLimit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTransactionLimit() {
        return transactionLimit;
    }

    public void setTransactionLimit(Double transactionLimit) {
        this.transactionLimit = transactionLimit;
    }
}
