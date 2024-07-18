package org.example.spring1.tradeOrder;

import lombok.RequiredArgsConstructor;
import org.example.spring1.asset.AssetService;
import org.example.spring1.currency.CurrencyService;
import org.example.spring1.exception.EntityNotFoundException;
import org.example.spring1.exception.InsufficientAssetsException;
import org.example.spring1.exception.InsufficientFundsException;
import org.example.spring1.exception.UserNotAuthenticatedException;
import org.example.spring1.tradeOrder.model.OrderStatus;
import org.example.spring1.tradeOrder.model.OrderType;
import org.example.spring1.tradeOrder.model.TradeOrder;
import org.example.spring1.tradeOrder.model.dto.TradeOrderDTO;
import org.example.spring1.user.UserDetailsImpl;
import org.example.spring1.user.UserRepository;
import org.example.spring1.user.model.User;
import org.example.spring1.userAssets.UserAssetsRepository;
import org.example.spring1.userAssets.UserAssetsService;
import org.example.spring1.userAssets.model.UserAssets;
import org.example.spring1.userFunds.UserFundsRepository;
import org.example.spring1.userFunds.UserFundsService;
import org.example.spring1.userFunds.model.UserFunds;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeOrderService {


    private final TradeOrderRepository tradeOrderRepository;
    private final UserRepository userRepository;
    private final AssetService assetService;
    private final TradeOrderMapper tradeOrderMapper;
    private final UserFundsRepository userFundsRepository;
    private final CurrencyService currencyService;
    private final UserAssetsRepository userAssetsRepository;
    private final UserFundsService userFundsService;
    private final UserAssetsService userAssetsService;

    public List<TradeOrderDTO> getAllTradeOrders() {
        return tradeOrderRepository.findAll().stream()
                .map(tradeOrderMapper::toTradeOrderDto)
                .collect(Collectors.toList());
    }

    public TradeOrderDTO getTradeOrderById(Long id) {
        return tradeOrderRepository.findById(id)
                .map(tradeOrderMapper::toTradeOrderDto)
                .orElseThrow(() -> new EntityNotFoundException("Trade order not found"));
    }

    public List<TradeOrderDTO> getMyTradeOrders() {
        Long id = getCurrentUserId();
        return tradeOrderRepository.findByUserId(id).stream()
                .map(tradeOrderMapper::toTradeOrderDto)
                .collect(Collectors.toList());
    }

    public List<TradeOrderDTO> getTradeOrdersByUserId(Long userId) {
        return tradeOrderRepository.findByUserId(userId).stream()
                .map(tradeOrderMapper::toTradeOrderDto)
                .collect(Collectors.toList());
    }

    public TradeOrderDTO createTradeOrderFromDTO(Long userId, TradeOrderDTO tradeOrderDTO) throws InsufficientFundsException, InsufficientAssetsException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        TradeOrder tradeOrder = tradeOrderMapper.toEntity(tradeOrderDTO);
        tradeOrder.setUser(user);
        tradeOrder.setOrderTime(LocalDateTime.now());
        tradeOrder.setStatus(OrderStatus.PENDING);

        BigDecimal price = tradeOrderDTO.getPrice() != null ? tradeOrderDTO.getPrice() : assetService.getCurrentPriceBySymbol(tradeOrder.getSymbol());
        tradeOrder.setPrice(price);

        if (tradeOrder.getOrderType() == OrderType.BUY) {
            BigDecimal totalCost = price.multiply(tradeOrder.getQuantity());
            userFundsService.allocateFundsForOrder(userId, totalCost);
        } else if (tradeOrder.getOrderType() == OrderType.SELL) {
            userAssetsService.deductAssets(user, tradeOrder.getSymbol(), tradeOrder.getQuantity());
        }

        TradeOrder savedOrder = tradeOrderRepository.save(tradeOrder);
        return tradeOrderMapper.toTradeOrderDto(savedOrder);
    }

    private void checkAssetAvailability(Long userId, String assetSymbol, BigDecimal quantity) throws InsufficientAssetsException {
        Optional<UserAssets> userAssets = userAssetsRepository.findByUserIdAndAssetSymbol(userId, assetSymbol);
        if (userAssets.isEmpty() || userAssets.get().getQuantity().compareTo(quantity) < 0) {
            throw new InsufficientAssetsException("User does not have enough of the asset to sell");
        }
    }

    private void checkFundsAvailability(Long userId, BigDecimal quantity, BigDecimal pricePerUnit) {
        BigDecimal totalCost = pricePerUnit.multiply(quantity);
        List<UserFunds> fundsList = userFundsRepository.findByUserId(userId);

        BigDecimal totalAvailableUSD = fundsList.stream()
                .map(funds -> currencyService.convertToUSD(funds.getCurrency(), funds.getAvailableAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAvailableUSD.compareTo(totalCost) < 0) {
            throw new InsufficientFundsException("User does not have sufficient funds to place this order.");
        }
    }

    public void cancelTradeOrder(Long id) {
        TradeOrder tradeOrder = tradeOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trade order not found"));

        if (!tradeOrder.getStatus().equals(OrderStatus.CANCELLED)) {
            tradeOrder.setStatus(OrderStatus.CANCELLED);
            tradeOrderRepository.save(tradeOrder);

            User user = tradeOrder.getUser();
            String assetSymbol = tradeOrder.getSymbol();
            BigDecimal quantity = tradeOrder.getQuantity();

            if (tradeOrder.getOrderType() == OrderType.SELL) {
                userAssetsService.updateUserAssets(user, assetService.getAssetBySymbol(assetSymbol), quantity, true);
            } else {
                BigDecimal price = tradeOrder.getPrice();
                BigDecimal totalAmount = price.multiply(quantity);
                userFundsService.cancelOrderUserFunds(user, totalAmount);
            }
        }
    }


    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).getId();
    }
}