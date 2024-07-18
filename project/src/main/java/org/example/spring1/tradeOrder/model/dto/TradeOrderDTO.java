package org.example.spring1.tradeOrder.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.spring1.tradeOrder.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeOrderDTO {
    private String symbol;
    private BigDecimal quantity;
    private String orderType;
    private BigDecimal price;
    private OrderStatus status;
    private LocalDateTime orderTime;
    private Long id;
}
