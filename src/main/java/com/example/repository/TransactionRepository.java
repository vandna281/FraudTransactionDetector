package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.entity.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}

