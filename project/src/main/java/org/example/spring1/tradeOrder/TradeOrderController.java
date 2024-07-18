package org.example.spring1.tradeOrder;

import lombok.RequiredArgsConstructor;
import org.example.spring1.exception.InsufficientFundsException;
import org.example.spring1.tradeOrder.model.dto.TradeMessageDTO;
import org.example.spring1.tradeOrder.model.dto.TradeOrderDTO;
import org.example.spring1.tradeOrder.model.dto.TradeOrderUpdateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tradeOrders")
@RequiredArgsConstructor
public class TradeOrderController {

    private final TradeOrderService tradeOrderService;

    @PostMapping
    public ResponseEntity<?> createTradeOrder(@RequestBody TradeOrderDTO tradeOrderDTO) {
        try {
            TradeOrderDTO savedTradeOrderDTO = tradeOrderService.createTradeOrderFromDTO(tradeOrderService.getCurrentUserId(), tradeOrderDTO);
            return ResponseEntity.ok(savedTradeOrderDTO);
        } catch (InsufficientFundsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TradeMessageDTO(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<TradeOrderDTO>> getAllTradeOrders() {
        List<TradeOrderDTO> tradeOrderDTOs = tradeOrderService.getAllTradeOrders();
        return ResponseEntity.ok(tradeOrderDTOs);
    }
    @GetMapping("/{id}")
    public ResponseEntity<TradeOrderDTO> getTradeOrderById(@PathVariable Long id) {
        TradeOrderDTO tradeOrderDTO = tradeOrderService.getTradeOrderById(id);
        return ResponseEntity.ok(tradeOrderDTO);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<TradeOrderDTO>> getMyTradeOrders() {
        List<TradeOrderDTO> tradeOrderDTO = tradeOrderService.getMyTradeOrders();
        return ResponseEntity.ok(tradeOrderDTO);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TradeOrderDTO>> getTradeOrdersByUserId(@PathVariable Long userId) {
        List<TradeOrderDTO> tradeOrders = tradeOrderService.getTradeOrdersByUserId(userId);
        return ResponseEntity.ok(tradeOrders);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelTradeOrder(@PathVariable Long id) {
        tradeOrderService.cancelTradeOrder(id);
        return ResponseEntity.ok().build();
    }

}