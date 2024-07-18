package org.example.spring1.jobs;

import org.example.spring1.asset.AssetService;
import org.example.spring1.asset.model.Asset;
import org.example.spring1.tradeOrder.TradeOrderRepository;
import org.example.spring1.tradeOrder.model.OrderType;
import org.example.spring1.tradeOrder.model.OrderStatus;
import org.example.spring1.tradeOrder.model.TradeOrder;
import org.example.spring1.transaction.TransactionRepository;
import org.example.spring1.transaction.model.Transaction;
import org.example.spring1.userAssets.UserAssetsService;
import org.example.spring1.userFunds.UserFundsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMatchingJob {


    private final TradeOrderRepository tradeOrderRepository;
    private final TransactionRepository transactionRepository;
    private final AssetService assetService;
    private final UserAssetsService userAssetsService;
    private final UserFundsService userFundsService;

    @Scheduled(fixedRate = 6000)
    public void executeTradeOrders() {
        List<TradeOrder> openOrders = tradeOrderRepository.findAllByStatus(OrderStatus.PENDING);

        openOrders.forEach(order -> {
            Asset asset = assetService.getAssetBySymbol(order.getSymbol());
            BigDecimal currentMarketPrice = assetService.getCurrentPriceBySymbol(asset.getSymbol());
            boolean isBuyOrder = OrderType.BUY.equals(order.getOrderType());
            boolean isSellOrder = OrderType.SELL.equals(order.getOrderType());
            BigDecimal executionPrice = determineExecutionPrice(order, currentMarketPrice, isBuyOrder, isSellOrder);

            if (executionPrice != null) {
                executeOrder(order, asset, executionPrice, isBuyOrder);
            }
        });
    }

    private BigDecimal determineExecutionPrice(TradeOrder order, BigDecimal marketPrice, boolean isBuy, boolean isSell) {
        if ((isBuy && marketPrice.compareTo(order.getPrice()) <= 0) ||
                (isSell && marketPrice.compareTo(order.getPrice()) >= 0)) {
            return isBuy ? marketPrice.min(order.getPrice()) : marketPrice.max(order.getPrice());
        }
        return null;
    }

    private void executeOrder(TradeOrder order, Asset asset, BigDecimal executedPrice, boolean isBuyOrder) {
        BigDecimal totalCost = executedPrice.multiply(order.getQuantity());
        if (isBuyOrder) {
            userFundsService.deductFundsForPurchase(order.getUser(), totalCost);
            userAssetsService.updateUserAssets(order.getUser(), asset, order.getQuantity(), true);
        } else {
            userFundsService.addFundsAfterSale(order.getUser(), order.getQuantity(), executedPrice);
        }

        Transaction transaction = Transaction.builder()
                .tradeOrder(order)
                .executedPrice(executedPrice)
                .quantity(order.getQuantity())
                .transactionTime(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
        order.setStatus(OrderStatus.COMPLETED);
        tradeOrderRepository.save(order);
    }
}