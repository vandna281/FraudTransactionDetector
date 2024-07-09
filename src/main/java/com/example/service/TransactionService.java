package com.example.service;

import com.example.entity.Transaction;
import com.example.entity.TransactionValidation;
import com.example.repository.TransactionRepository;
import com.example.repository.TransactionValidationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private TransactionRepository trxnRepo;

    @Autowired
    private TransactionValidationRepository validationRepo;

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @Autowired
    private RedisTemplate<String, Double> redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public boolean validateTransaction(Transaction transaction) {
        logger.info("Validating the transaction");
        transaction = updatingTransactionMetadata(transaction);
        boolean isFraud = isFraudTrxn(transaction);
        if (isFraud) {
            logger.info("Handling fraudulent transaction '{}' event by publishing it to kafka topic", transaction.getId());
            kafkaTemplate.send("fraud_transactions", "Fraud detected for transaction: " + transaction.getId());
            transaction.setFraudulent(true);
            logger.info("Updating transaction status in transaction table for {}", transaction.getId());
            updatingTransactionMetadata(transaction);
        }
        return isFraud;
    }

    private boolean isFraudTrxn(Transaction transaction) {
        Double rules = redisTemplate.opsForValue().get("transaction_limit");
        if (rules == null) {
            rules = fetchRulesFromDB();
            redisTemplate.opsForValue().set("transaction_limit", rules);
        }
        boolean isFraud = fraudDetectionService.isFraudulent(transaction, rules);
        return isFraud;
    }

    @KafkaListener(topics = "fraud_transactions", groupId = "fraud-detection-group")
    public void receive(String transactionMsg) {
        System.out.println("Received fradulent transaction : " + transactionMsg);
    }

    private Transaction updatingTransactionMetadata(Transaction transaction){
        return trxnRepo.save(transaction);
    }

    private Double fetchRulesFromDB() {
        List<TransactionValidation> validation = validationRepo.findAll();
        return validation.get(0).getTransactionLimit();
    }

    public boolean checkTransactionStatus(Long transactionId) {
        Transaction trxn = trxnRepo.getReferenceById(transactionId);
        return isFraudTrxn(trxn);
    }
}
