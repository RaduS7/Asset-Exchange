package org.example.spring1.currency;

import org.example.spring1.currency.model.Currency;
import org.example.spring1.currency.model.CurrencyCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    @AfterEach
    void tearDown() {
        currencyRepository.deleteAll();
    }

    @Test
    void findByCurrencyCode_ExistingCurrencyCode_ReturnsCurrency() {
        Currency currency = new Currency(null, CurrencyCode.EUR, new BigDecimal("1.18"));
        currencyRepository.save(currency);

        Optional<Currency> optionalCurrency = currencyRepository.findByCurrencyCode(CurrencyCode.EUR);

        assertTrue(optionalCurrency.isPresent());
        assertEquals(currency.getCurrencyCode(), optionalCurrency.get().getCurrencyCode());
    }

    @Test
    void findByCurrencyCode_NonExistingCurrencyCode_ReturnsEmpty() {
        Optional<Currency> optionalCurrency = currencyRepository.findByCurrencyCode(CurrencyCode.EUR);

        assertTrue(optionalCurrency.isEmpty());
    }
}