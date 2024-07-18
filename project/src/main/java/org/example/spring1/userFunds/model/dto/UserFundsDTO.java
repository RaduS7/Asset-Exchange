package org.example.spring1.userFunds.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.spring1.currency.model.CurrencyCode;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFundsDTO {
    private CurrencyCode currency;
    private BigDecimal amount;
}
