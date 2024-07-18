package org.example.spring1.currency;

import lombok.RequiredArgsConstructor;
import org.example.spring1.currency.model.Currency;
import org.example.spring1.currency.model.CurrencyCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public BigDecimal convertToUSD(CurrencyCode currencyCode, BigDecimal amount) {
        if (currencyCode == CurrencyCode.USD) {
            return amount;
        }
        return convertCurrency(amount, currencyCode, true);
    }

    public BigDecimal convertFromUSD(CurrencyCode targetCurrencyCode, BigDecimal amountInUSD) {
        if (targetCurrencyCode == CurrencyCode.USD) {
            return amountInUSD;
        }
        return convertCurrency(amountInUSD, targetCurrencyCode, false);
    }

    private BigDecimal convertCurrency(BigDecimal amount, CurrencyCode currencyCode, boolean toUsd) {
        Optional<Currency> currencyOpt = currencyRepository.findByCurrencyCode(currencyCode);
        if (currencyOpt.isEmpty()) {
            throw new IllegalStateException("Currency conversion rate not found for " + currencyCode);
        }
        Currency currency = currencyOpt.get();
        BigDecimal rate = currency.getRateToUsd();
        return toUsd ? amount.multiply(rate) : amount.divide(rate, 6, BigDecimal.ROUND_HALF_UP);
    }
}
