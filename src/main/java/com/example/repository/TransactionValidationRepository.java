package com.example.repository;

import com.example.entity.TransactionValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionValidationRepository extends JpaRepository<TransactionValidation, Long> {
}
