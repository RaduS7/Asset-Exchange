package org.example.spring1.currency.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "currency_code", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private CurrencyCode currencyCode;

    @Column(name = "rate_to_usd", nullable = false, precision = 10, scale = 6)
    private BigDecimal rateToUsd;
}
