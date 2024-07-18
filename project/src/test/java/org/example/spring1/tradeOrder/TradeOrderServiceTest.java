package org.example.spring1.tradeOrder;

import org.example.spring1.asset.AssetService;
import org.example.spring1.exception.EntityNotFoundException;
import org.example.spring1.tradeOrder.model.OrderStatus;
import org.example.spring1.tradeOrder.model.OrderType;
import org.example.spring1.tradeOrder.model.TradeOrder;
import org.example.spring1.user.model.User;
import org.example.spring1.userAssets.UserAssetsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TradeOrderServiceTest {

    @Mock
    private TradeOrderRepository tradeOrderRepository;
    @Mock
    private UserAssetsService userAssetsService;
    @Mock
    private AssetService assetService;

    @InjectMocks
    private TradeOrderService tradeOrderService;

    private TradeOrder tradeOrder;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        tradeOrder = TradeOrder.builder()
                .id(1L)
                .symbol("BTC")
                .quantity(new BigDecimal("2"))
                .price(new BigDecimal("50000"))
                .orderType(OrderType.SELL)
                .status(OrderStatus.PENDING)
                .orderTime(LocalDateTime.now())
                .user(user)
                .build();
    }

    @Test
    void cancelTradeOrder_Successful() {
        when(tradeOrderRepository.findById(1L)).thenReturn(Optional.of(tradeOrder));
        when(assetService.getAssetBySymbol("BTC")).thenReturn(null);
        doNothing().when(userAssetsService).updateUserAssets(any(User.class), any(), any(BigDecimal.class), anyBoolean());

        tradeOrderService.cancelTradeOrder(1L);

        assertEquals(OrderStatus.CANCELLED, tradeOrder.getStatus());
        verify(tradeOrderRepository).save(tradeOrder);
        verify(userAssetsService).updateUserAssets(eq(user), any(), eq(new BigDecimal("2")), eq(true));
    }

    @Test
    void cancelTradeOrder_AlreadyCancelled() {
        tradeOrder.setStatus(OrderStatus.CANCELLED);
        when(tradeOrderRepository.findById(1L)).thenReturn(Optional.of(tradeOrder));

        tradeOrderService.cancelTradeOrder(1L);

        verify(tradeOrderRepository, never()).save(tradeOrder);
    }

    @Test
    void cancelTradeOrder_NonExistentOrder() {
        when(tradeOrderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tradeOrderService.cancelTradeOrder(1L));
    }
}
