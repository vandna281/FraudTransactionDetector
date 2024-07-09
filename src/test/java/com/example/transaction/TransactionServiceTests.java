package com.example.transaction;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.entity.Transaction;
import com.example.entity.TransactionValidation;
import com.example.repository.TransactionRepository;
import com.example.repository.TransactionValidationRepository;
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

import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {
    @Mock
    private TransactionRepository repo;

    @Mock
    private TransactionValidationRepository validationRepository;

    @Mock
    private FraudDetectionService fraudDetectionService;

    @Mock
    private RedisTemplate<String, Double> redisTemplate;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private ValueOperations<String, Double> valueOperations;

    private Transaction transaction;

    public static final Double transactionLimit = 100000.0;

    @BeforeEach
    public void setUp() {
        transaction = new Transaction();
        transaction.setId(1L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testValidateTransaction_NoRulesInRedis_NotFraudulent() {
        when(valueOperations.get("transaction_limit")).thenReturn(null);
        when(validationRepository.findAll()).thenReturn(Arrays.asList(
                new TransactionValidation(1L, 100000.0)
        ));
        when(repo.save(any(Transaction.class))).thenReturn(transaction);
        when(fraudDetectionService.isFraudulent(any(Transaction.class), eq(transactionLimit))).thenReturn(false);

        boolean result = transactionService.validateTransaction(transaction);

        verify(valueOperations).set("transaction_limit", transactionLimit);
        verify(repo, times(1)).save(transaction);
        verify(kafkaTemplate, never()).send(anyString(), anyString());
        assertFalse(result);
    }

    @Test
    public void testValidateTransaction_RulesInRedis_NotFraudulent() {
        when(valueOperations.get("transaction_limit")).thenReturn(transactionLimit);
        when(repo.save(any(Transaction.class))).thenReturn(transaction);
        when(fraudDetectionService.isFraudulent(any(Transaction.class), eq(transactionLimit))).thenReturn(false);

        boolean result = transactionService.validateTransaction(transaction);

        verify(valueOperations, never()).set(anyString(), anyDouble());
        verify(repo, times(1)).save(transaction);
        verify(kafkaTemplate, never()).send(anyString(), anyString());
        assertFalse(result);
    }

    @Test
    public void testValidateTransaction_NoRulesInRedis_Fraudulent() {
        when(valueOperations.get("transaction_limit")).thenReturn(null);
        when(validationRepository.findAll()).thenReturn(Arrays.asList(
                new TransactionValidation(1L, 100000.0)
        ));
        when(repo.save(any(Transaction.class))).thenReturn(transaction);
        when(fraudDetectionService.isFraudulent(any(Transaction.class), eq(transactionLimit))).thenReturn(true);

        boolean result = transactionService.validateTransaction(transaction);

        verify(valueOperations).set("transaction_limit", transactionLimit);
        verify(repo, times(2)).save(transaction);
        verify(kafkaTemplate).send(eq("fraud_transactions"), eq("Fraud detected for transaction: " + transaction.getId()));
        assertTrue(result);
    }

    @Test
    public void testValidateTransaction_RulesInRedis_Fraudulent() {
        when(valueOperations.get("transaction_limit")).thenReturn(transactionLimit);
        when(repo.save(any(Transaction.class))).thenReturn(transaction);
        when(fraudDetectionService.isFraudulent(any(Transaction.class), eq(transactionLimit))).thenReturn(true);

        boolean result = transactionService.validateTransaction(transaction);

        verify(valueOperations, never()).set(anyString(), anyDouble());
        verify(repo, times(2)).save(transaction);
        verify(kafkaTemplate).send(eq("fraud_transactions"), eq("Fraud detected for transaction: " + transaction.getId()));
        assertTrue(result);
    }

    @Test
    public void testCheckTransactionStatus() {
        when(validationRepository.findAll()).thenReturn(Arrays.asList(
                new TransactionValidation(1L, 100000.0)
        ));
        boolean status = transactionService.checkTransactionStatus(1L);
        assertFalse(status);
        verify(repo, times(1)).getReferenceById(1L);
    }

}