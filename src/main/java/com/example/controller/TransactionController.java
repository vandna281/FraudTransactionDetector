package com.example.controller;

import com.example.entity.Transaction;
import com.example.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService trxnService;

    @PostMapping("/transactions")
    public String checkTransaction(@RequestBody Transaction transaction) {
        boolean status = trxnService.validateTransaction(transaction);
        return "Transaction Fraud status is : " + status;
    }
}
