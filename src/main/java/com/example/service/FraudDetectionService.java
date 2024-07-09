package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.entity.Transaction;

@Service
public class FraudDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(FraudDetectionService.class);

    public boolean isFraudulent(Transaction transaction, Double transactionLimit) {
        logger.info("Checking if transaction id {} fraudulent or not", transaction.getId());
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

