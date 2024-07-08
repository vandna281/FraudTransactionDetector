package com.example.transaction;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.entity.Transaction;
import com.example.repository.TransactionRepository;
import com.example.service.FraudDetectionService;
import com.example.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {
    @Mock
    private TransactionRepository repo;

    @Mock
    private FraudDetectionService fraudDetectionService;

    @Mock
    private RedisTemplate<String, Integer> redisTemplate;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private ValueOperations<String, Integer> valueOperations;

    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        transaction = new Transaction();
        transaction.setId(1L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testValidateTransaction_NoRulesInRedis_NotFraudulent() {
        when(valueOperations.get("transaction_limit")).thenReturn(null);
        when(repo.save(any(Transaction.class))).thenReturn(transaction);
        when(fraudDetectionService.isFraudulent(any(Transaction.class), eq(TransactionService.transactionLimit))).thenReturn(false);

        boolean result = transactionService.validateTransaction(transaction);

        verify(valueOperations).set("transaction_limit", TransactionService.transactionLimit);
        verify(repo, times(1)).save(transaction);
        verify(kafkaTemplate, never()).send(anyString(), anyString());
        assertFalse(result);
    }

    @Test
    public void testValidateTransaction_RulesInRedis_NotFraudulent() {
        when(valueOperations.get("transaction_limit")).thenReturn(TransactionService.transactionLimit);
        when(repo.save(any(Transaction.class))).thenReturn(transaction);
        when(fraudDetectionService.isFraudulent(any(Transaction.class), eq(TransactionService.transactionLimit))).thenReturn(false);

        boolean result = transactionService.validateTransaction(transaction);

        verify(valueOperations, never()).set(anyString(), anyInt());
        verify(repo, times(1)).save(transaction);
        verify(kafkaTemplate, never()).send(anyString(), anyString());
        assertFalse(result);
    }

    @Test
    public void testValidateTransaction_NoRulesInRedis_Fraudulent() {
        when(valueOperations.get("transaction_limit")).thenReturn(null);
        when(repo.save(any(Transaction.class))).thenReturn(transaction);
        when(fraudDetectionService.isFraudulent(any(Transaction.class), eq(TransactionService.transactionLimit))).thenReturn(true);

        boolean result = transactionService.validateTransaction(transaction);

        verify(valueOperations).set("transaction_limit", TransactionService.transactionLimit);
        verify(repo, times(2)).save(transaction);
        verify(kafkaTemplate).send(eq("fraud_transactions"), eq("Fraud detected for transaction: " + transaction.getId()));
        assertTrue(result);
    }

    @Test
    public void testValidateTransaction_RulesInRedis_Fraudulent() {
        when(valueOperations.get("transaction_limit")).thenReturn(TransactionService.transactionLimit);
        when(repo.save(any(Transaction.class))).thenReturn(transaction);
        when(fraudDetectionService.isFraudulent(any(Transaction.class), eq(TransactionService.transactionLimit))).thenReturn(true);

        boolean result = transactionService.validateTransaction(transaction);

        verify(valueOperations, never()).set(anyString(), anyInt());
        verify(repo, times(2)).save(transaction);
        verify(kafkaTemplate).send(eq("fraud_transactions"), eq("Fraud detected for transaction: " + transaction.getId()));
        assertTrue(result);
    }

}
