package com.example.transaction;

import com.example.entity.Transaction;
import com.example.service.FraudDetectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FraudDetectionServiceTests {

    @Mock
    private FraudDetectionService fraudDetectionService;

    @BeforeEach
    public void setUp() {
        when(fraudDetectionService.isFraudulent(any(Transaction.class), any())).thenCallRealMethod();
    }

    @Test
    public void testHighTransactionAmount() {
        Transaction transaction = new Transaction(150000, "USA", "USA");
        assertTrue(fraudDetectionService.isFraudulent(transaction, 100000.0), "Transaction with high amount should be flagged as fraudulent");
    }

    @Test
    public void testCrossBorderTransaction() {
        Transaction transaction = new Transaction(50000, "USA", "CAN");
        assertTrue(fraudDetectionService.isFraudulent(transaction, 100000.0), "Cross-border transaction should be flagged as fraudulent");
    }

    @Test
    public void testNormalTransaction() {
        Transaction transaction = new Transaction(50000, "USA", "USA");
        assertFalse(fraudDetectionService.isFraudulent(transaction, 100000.0), "Normal transaction should not be flagged as fraudulent");
    }

    @Test
    public void testHighAmountCrossBorderTransaction() {
        Transaction transaction = new Transaction(150000, "USA", "CAN");
        assertTrue(fraudDetectionService.isFraudulent(transaction, 100000.0), "High amount cross-border transaction should be flagged as fraudulent");
    }

    @Test
    public void testEdgeCaseHighAmount() {
        Transaction transaction = new Transaction(100000, "USA", "USA");
        assertFalse(fraudDetectionService.isFraudulent(transaction, 100000.0), "Transaction with amount exactly at the threshold should not be flagged as fraudulent");
    }

    @Test
    public void testEdgeCaseSameCountry() {
        Transaction transaction = new Transaction(50000, "USA", "USA");
        assertFalse(fraudDetectionService.isFraudulent(transaction, 100000.0), "Transaction within the same country should not be flagged as fraudulent");
    }

}
