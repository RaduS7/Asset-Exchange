package org.example.spring1.transaction.model;


import jakarta.persistence.*;
import lombok.*;
import org.example.spring1.tradeOrder.model.TradeOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trade_order_id", nullable = false)
    private TradeOrder tradeOrder;

    @Column(name = "executed_price", nullable = false)
    private BigDecimal executedPrice;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;
}