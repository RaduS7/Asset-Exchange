package org.example.spring1.tradeOrder.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeMessageDTO {
    private String message;
}
