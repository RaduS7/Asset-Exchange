package org.example.spring1.currency;

import org.example.spring1.currency.model.Currency;
import org.example.spring1.currency.model.CurrencyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CurrencyService currencyService;

    private Currency euroCurrency;

    @BeforeEach
    void setUp() {
        euroCurrency = Currency.builder()
                .id(1L)
                .currencyCode(CurrencyCode.EUR)
                .rateToUsd(new BigDecimal("1.2"))
                .build();
    }

    @Test
    void convertToUSD_WithNonUSDInput_ShouldConvertCorrectly() {
        when(currencyRepository.findByCurrencyCode(CurrencyCode.EUR)).thenReturn(Optional.of(euroCurrency));
        BigDecimal amount = new BigDecimal("100");
        BigDecimal expected = amount.multiply(euroCurrency.getRateToUsd());
        BigDecimal result = currencyService.convertToUSD(CurrencyCode.EUR, amount);
        assertEquals(expected, result);
    }

    @Test
    void convertToUSD_WithUSDInput_ShouldReturnSameAmount() {
        BigDecimal amount = new BigDecimal("100");
        BigDecimal result = currencyService.convertToUSD(CurrencyCode.USD, amount);
        assertEquals(amount, result);
    }

    @Test
    void convertFromUSD_WithNonUSDTarget_ShouldConvertCorrectly() {
        when(currencyRepository.findByCurrencyCode(CurrencyCode.EUR)).thenReturn(Optional.of(euroCurrency));
        BigDecimal amountInUSD = new BigDecimal("100");
        BigDecimal expected = amountInUSD.divide(euroCurrency.getRateToUsd(), 6, BigDecimal.ROUND_HALF_UP);
        BigDecimal result = currencyService.convertFromUSD(CurrencyCode.EUR, amountInUSD);
        assertEquals(expected, result);
    }

    @Test
    void convertFromUSD_WithUSDTarget_ShouldReturnSameAmount() {
        BigDecimal amountInUSD = new BigDecimal("100");
        BigDecimal result = currencyService.convertFromUSD(CurrencyCode.USD, amountInUSD);
        assertEquals(amountInUSD, result);
    }

    @Test
    void convertCurrency_CurrencyNotFound_ShouldThrowException() {
        when(currencyRepository.findByCurrencyCode(CurrencyCode.EUR)).thenReturn(Optional.empty());
        BigDecimal amount = new BigDecimal("100");
        assertThrows(IllegalStateException.class, () -> currencyService.convertToUSD(CurrencyCode.EUR, amount));
    }
}
