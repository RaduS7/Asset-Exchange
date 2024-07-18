package org.example.spring1.currency;

import org.example.spring1.currency.model.Currency;
import org.example.spring1.currency.model.CurrencyCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findByCurrencyCode(CurrencyCode currencyCode);
}