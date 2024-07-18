package org.example.spring1.tradeOrder;

import org.example.spring1.tradeOrder.model.OrderStatus;
import org.example.spring1.tradeOrder.model.TradeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeOrderRepository extends JpaRepository<TradeOrder, Long> {
    List<TradeOrder> findByUserId(Long userId);
    List<TradeOrder> findAllByStatus(OrderStatus orderStatus);
}
