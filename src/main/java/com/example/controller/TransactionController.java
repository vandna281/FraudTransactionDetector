package com.example.controller;

import com.example.entity.Transaction;
import com.example.service.FraudDetectionService;
import com.example.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService trxnService;

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @PostMapping("/transactions")
    public String checkTransaction(@RequestBody Transaction transaction) {
        logger.info("New incoming transaction with amount : {}", transaction.getAmount());
        boolean status = trxnService.validateTransaction(transaction);
        return "Transaction status is : " + status;
    }

    @GetMapping("/transactions/{transactionId}/status")
    public String checkTransactionStatus(@PathVariable String transactionId) {
        boolean status = trxnService.checkTransactionStatus(Long.valueOf(transactionId));
        logger.info("Transaction fraud status for trxnId : {} is {}", transactionId, status);
        return "Transaction Fraud status is : " + status;
    }
}
