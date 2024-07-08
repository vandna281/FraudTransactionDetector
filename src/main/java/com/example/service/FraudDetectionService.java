package com.example.service;

import org.springframework.stereotype.Service;
import com.example.entity.Transaction;

@Service
public class FraudDetectionService {

    public boolean isFraudulent(Transaction transaction, Integer transactionLimit) {
        //High transaction amount threshold
        if (transaction.getAmount() > transactionLimit) {
            return true;
        }
        //Cross-border transaction check
        if (!transaction.getSourceCountry().equals(transaction.getDestinationCountry())) {
            return true;
        }
        return false;
    }

}

