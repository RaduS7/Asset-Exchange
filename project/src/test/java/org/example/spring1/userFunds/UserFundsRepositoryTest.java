package org.example.spring1.userFunds;

import org.example.spring1.currency.model.CurrencyCode;
import org.example.spring1.user.model.User;
import org.example.spring1.userFunds.model.UserFunds;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserFundsRepositoryTest {

    @Autowired
    private UserFundsRepository userFundsRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    @BeforeEach
    public void setup() {
        testUser = User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("testpassword")
                .build();
        entityManager.persist(testUser);
    }

    @AfterEach
    public void cleanup() {
        entityManager.clear();
    }

    @Test
    public void whenFindByUserId_thenReturnsUserFunds() {
        UserFunds funds = UserFunds.builder()
                .user(testUser)
                .currency(CurrencyCode.USD)
                .totalAmount(new BigDecimal("1000.00"))
                .availableAmount(new BigDecimal("800.00"))
                .pendingAmount(new BigDecimal("200.00"))
                .build();
        entityManager.persist(funds);

        List<UserFunds> foundFunds = userFundsRepository.findByUserId(testUser.getId());

        assertFalse(foundFunds.isEmpty());
        assertEquals(1, foundFunds.size());
        assertEquals(funds.getCurrency(), foundFunds.get(0).getCurrency());
    }

    @Test
    public void whenFindByUserIdAndCurrency_thenReturnsUserFunds() {
        UserFunds funds = UserFunds.builder()
                .user(testUser)
                .currency(CurrencyCode.EUR)
                .totalAmount(new BigDecimal("500.00"))
                .availableAmount(new BigDecimal("300.00"))
                .pendingAmount(new BigDecimal("200.00"))
                .build();
        entityManager.persist(funds);

        Optional<UserFunds> foundFunds = userFundsRepository.findByUserIdAndCurrency(testUser.getId(), CurrencyCode.EUR);

        assertTrue(foundFunds.isPresent());
        assertEquals(funds.getTotalAmount(), foundFunds.get().getTotalAmount());
    }
}