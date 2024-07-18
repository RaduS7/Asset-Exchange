package org.example.spring1.transaction;

import org.example.spring1.tradeOrder.model.OrderStatus;
import org.example.spring1.tradeOrder.model.OrderType;
import org.example.spring1.tradeOrder.model.TradeOrder;
import org.example.spring1.transaction.model.Transaction;
import org.example.spring1.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TestEntityManager entityManager;

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
    }

    @Test
    void saveAndFindTransactionById() {
        User user = User.builder()
                .username("john_doe")
                .email("john.doe@example.com")
                .password("securePassword123")
                .build();
        user = entityManager.persist(user);

        TradeOrder order1 = TradeOrder.builder()
                .symbol("AAPL")
                .quantity(new BigDecimal("10"))
                .price(new BigDecimal("150.00"))
                .orderType(OrderType.BUY)
                .status(OrderStatus.PENDING)
                .orderTime(LocalDateTime.now())
                .user(user)
                .build();
        order1 = entityManager.persist(order1);

        Transaction transaction = Transaction.builder()
                .tradeOrder(order1)
                .executedPrice(BigDecimal.valueOf(123L))
                .quantity(order1.getQuantity())
                .transactionTime(LocalDateTime.now())
                .build();


        transaction = entityManager.persistFlushFind(transaction);

        Transaction foundTransaction = transactionRepository.findById(transaction.getId()).orElse(null);

        assertNotNull(foundTransaction, "The transaction should not be null");
        assertEquals(transaction.getExecutedPrice(), foundTransaction.getExecutedPrice(), "Executed prices should match");
        assertEquals(transaction.getQuantity(), foundTransaction.getQuantity(), "Quantities should match");
        assertEquals(transaction.getTransactionTime().truncatedTo(java.time.temporal.ChronoUnit.SECONDS),
                foundTransaction.getTransactionTime().truncatedTo(java.time.temporal.ChronoUnit.SECONDS),
                "Transaction times should match but truncated to seconds for comparison");
    }
}