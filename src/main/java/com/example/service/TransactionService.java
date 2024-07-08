package com.example.service;

import com.example.entity.Transaction;
import com.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repo;

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public static final Integer transactionLimit = 100000;

    public boolean validateTransaction(Transaction transaction) {
        transaction = saveTransactionMetadata(transaction);
        Integer rules = redisTemplate.opsForValue().get("transaction_limit");
        if (rules == null) {
            rules = fetchRulesFromDB();
            redisTemplate.opsForValue().set("transaction_limit", rules);
        }

        boolean isFraud = fraudDetectionService.isFraudulent(transaction, rules);
        if (isFraud) {
            kafkaTemplate.send("fraud_transactions", "Fraud detected for transaction: " + transaction.getId());
            transaction.setFraudulent(true);
            repo.save(transaction);
        }
        return isFraud;
    }

    @KafkaListener(topics = "fraud_transactions", groupId = "fraud-detection-group")
    public void receive(String transactionMsg) {
        System.out.println("Received fradulent transaction : " + transactionMsg);
    }

    private Transaction saveTransactionMetadata(Transaction transaction){
        return repo.save(transaction);
    }

    private Integer fetchRulesFromDB() {
        // Fetch rules from database or external service
        return transactionLimit;
    }
}
