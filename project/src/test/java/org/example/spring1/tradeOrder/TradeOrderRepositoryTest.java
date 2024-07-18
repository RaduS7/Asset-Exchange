package org.example.spring1.tradeOrder;

import org.example.spring1.tradeOrder.model.OrderStatus;
import org.example.spring1.tradeOrder.model.OrderType;
import org.example.spring1.tradeOrder.model.TradeOrder;
import org.example.spring1.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TradeOrderRepositoryTest {

    @Autowired
    private TradeOrderRepository tradeOrderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @AfterEach
    void tearDown() {
        tradeOrderRepository.deleteAll();
    }

    @Test
    void findByUserId_ExistingUserId_ReturnsOrders() {
        User user = User.builder()
                .username("john_doe")
                .email("john.doe@example.com")
                .password("securePassword123")
                .build();
        entityManager.persist(user);

        TradeOrder order1 = TradeOrder.builder()
                .symbol("AAPL")
                .quantity(new BigDecimal("10"))
                .price(new BigDecimal("150.00"))
                .orderType(OrderType.BUY)
                .status(OrderStatus.PENDING)
                .orderTime(LocalDateTime.now())
                .user(user)
                .build();
        TradeOrder order2 = TradeOrder.builder()
                .symbol("GOOGL")
                .quantity(new BigDecimal("15"))
                .price(new BigDecimal("1200.00"))
                .orderType(OrderType.SELL)
                .status(OrderStatus.PENDING)
                .orderTime(LocalDateTime.now())
                .user(user)
                .build();
        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.flush();

        List<TradeOrder> result = tradeOrderRepository.findByUserId(user.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(o -> o.getSymbol().equals("AAPL")));
        assertTrue(result.stream().anyMatch(o -> o.getSymbol().equals("GOOGL")));
    }

    @Test
    void findByUserId_NonExistingUserId_ReturnsEmptyList() {
        List<TradeOrder> result = tradeOrderRepository.findByUserId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByStatus_ExistingStatus_ReturnsOrders() {
        User user = User.builder()
                .username("john_doe")
                .email("john.doe@example.com")
                .password("securePassword123")
                .build();
        entityManager.persist(user);

        TradeOrder order1 = TradeOrder.builder()
                .symbol("AAPL")
                .quantity(new BigDecimal("10"))
                .price(new BigDecimal("150.00"))
                .orderType(OrderType.BUY)
                .status(OrderStatus.PENDING)
                .orderTime(LocalDateTime.now())
                .user(user)
                .build();
        TradeOrder order2 = TradeOrder.builder()
                .symbol("MSFT")
                .quantity(new BigDecimal("20"))
                .price(new BigDecimal("300.00"))
                .orderType(OrderType.SELL)
                .status(OrderStatus.COMPLETED)
                .orderTime(LocalDateTime.now())
                .user(user)
                .build();
        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.flush();

        List<TradeOrder> resultPending = tradeOrderRepository.findAllByStatus(OrderStatus.PENDING);
        List<TradeOrder> resultClosed = tradeOrderRepository.findAllByStatus(OrderStatus.COMPLETED);

        assertEquals(1, resultPending.size());
        assertTrue(resultPending.stream().anyMatch(o -> o.getSymbol().equals("AAPL")));
        assertEquals(1, resultClosed.size());
        assertTrue(resultClosed.stream().anyMatch(o -> o.getSymbol().equals("MSFT")));
    }

    @Test
    void findAllByStatus_NonExistingStatus_ReturnsEmptyList() {
        List<TradeOrder> result = tradeOrderRepository.findAllByStatus(OrderStatus.CANCELLED);

        assertTrue(result.isEmpty());
    }
}