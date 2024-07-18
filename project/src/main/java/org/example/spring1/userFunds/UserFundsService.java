package org.example.spring1.userFunds;

import lombok.RequiredArgsConstructor;
import org.example.spring1.currency.CurrencyService;
import org.example.spring1.currency.model.CurrencyCode;
import org.example.spring1.exception.EntityNotFoundException;
import org.example.spring1.exception.InsufficientFundsException;
import org.example.spring1.user.UserDetailsImpl;
import org.example.spring1.user.UserRepository;
import org.example.spring1.user.model.User;
import org.example.spring1.userFunds.model.UserFunds;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserFundsService {

    private final UserFundsRepository userFundsRepository;
    private final CurrencyService currencyService;
    private final UserRepository userRepository;

    @Transactional
    public UserFunds addOrInitializeFunds(Long userId, CurrencyCode currency, BigDecimal amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User not found"));
        UserFunds funds = userFundsRepository.findByUserIdAndCurrency(userId, currency)
                .orElseGet(() -> createNewUserFunds(user, currency));

        funds.setTotalAmount(funds.getTotalAmount().add(amount));
        funds.setAvailableAmount(funds.getAvailableAmount().add(amount));
        return userFundsRepository.save(funds);
    }

    @Transactional
    public UserFunds withdrawFunds(Long userId, CurrencyCode currency, BigDecimal amount) {
        UserFunds funds = userFundsRepository.findByUserIdAndCurrency(userId, currency)
                .orElseThrow(() -> new IllegalStateException("Funds not found for the specified currency"));

        if (funds.getAvailableAmount().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        funds.setAvailableAmount(funds.getAvailableAmount().subtract(amount));
        funds.setTotalAmount(funds.getTotalAmount().subtract(amount));
        return userFundsRepository.save(funds);
    }

    private UserFunds createNewUserFunds(User user, CurrencyCode currency) {
        return UserFunds.builder()
                .user(user)
                .currency(currency)
                .totalAmount(BigDecimal.ZERO)
                .availableAmount(BigDecimal.ZERO)
                .pendingAmount(BigDecimal.ZERO)
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserFunds> getUserFundsByUserId(Long userId) {
        return userFundsRepository.findByUserId(userId);
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).getId();
    }

    @Transactional
    public void deductFundsForPurchase(User user, BigDecimal totalCostUSD) {
        List<UserFunds> funds = userFundsRepository.findByUserId(user.getId());
        BigDecimal remainingCost = totalCostUSD;

        // Iterate over the funds to calculate the total available in USD and adjust the funds
        for (UserFunds fund : funds) {
            BigDecimal availableInUSD = currencyService.convertToUSD(fund.getCurrency(), fund.getAvailableAmount());
            BigDecimal pendingInUSD = currencyService.convertToUSD(fund.getCurrency(), fund.getPendingAmount());
            BigDecimal totalAvailable = availableInUSD.add(pendingInUSD);

            if (totalAvailable.compareTo(remainingCost) >= 0) {
                // Enough funds in this account to cover the remaining cost
                BigDecimal costInCurrency = currencyService.convertFromUSD(fund.getCurrency(), remainingCost);
                BigDecimal newAvailable = fund.getAvailableAmount().add(fund.getPendingAmount()).subtract(costInCurrency);
                BigDecimal newPending = BigDecimal.ZERO;

                fund.setAvailableAmount(newAvailable);
                fund.setPendingAmount(newPending);
                fund.setTotalAmount(fund.getTotalAmount().subtract(costInCurrency));
                userFundsRepository.save(fund);
                remainingCost = BigDecimal.ZERO;
                break;
            } else {
                // Not enough funds in this account to cover the entire remaining cost, use what's available
                remainingCost = remainingCost.subtract(totalAvailable);
                BigDecimal totalReductionInCurrency = fund.getAvailableAmount().add(fund.getPendingAmount());

                fund.setAvailableAmount(BigDecimal.ZERO);
                fund.setPendingAmount(BigDecimal.ZERO);
                fund.setTotalAmount(fund.getTotalAmount().subtract(totalReductionInCurrency));
                userFundsRepository.save(fund);
            }
        }

        if (remainingCost.compareTo(BigDecimal.ZERO) > 0) {
            throw new InsufficientFundsException("User does not have enough funds to complete the purchase after considering all accounts.");
        }


    }


    @Transactional
    public void allocateFundsForOrder(Long userId, BigDecimal totalCostUSD) throws InsufficientFundsException {
        List<UserFunds> userFundsList = userFundsRepository.findByUserId(userId);
        BigDecimal totalAvailableUSD = BigDecimal.ZERO;

        // First, calculate the total available funds in USD
        for (UserFunds fund : userFundsList) {
            BigDecimal availableInUSD = currencyService.convertToUSD(fund.getCurrency(), fund.getAvailableAmount());
            totalAvailableUSD = totalAvailableUSD.add(availableInUSD);
        }

        // Check if the total available funds are sufficient
        if (totalAvailableUSD.compareTo(totalCostUSD) < 0) {
            throw new InsufficientFundsException("User does not have sufficient funds to place this order.");
        }

        // Deduct funds from each account as necessary
        BigDecimal remainingCostUSD = totalCostUSD;

        for (UserFunds fund : userFundsList) {
            if (remainingCostUSD.compareTo(BigDecimal.ZERO) <= 0) {
                break;  // Stop if the cost is fully allocated
            }

            BigDecimal availableInUSD = currencyService.convertToUSD(fund.getCurrency(), fund.getAvailableAmount());

            if (availableInUSD.compareTo(remainingCostUSD) >= 0) {
                // This fund alone is enough to cover the remaining cost
                BigDecimal costInCurrency = currencyService.convertFromUSD(fund.getCurrency(), remainingCostUSD);

                fund.setAvailableAmount(fund.getAvailableAmount().subtract(costInCurrency));
                fund.setPendingAmount(fund.getPendingAmount().add(costInCurrency));
            } else {
                // Use up all of this fund, still need more funds from other accounts
                remainingCostUSD = remainingCostUSD.subtract(availableInUSD);
                fund.setPendingAmount(fund.getAvailableAmount());
                fund.setAvailableAmount(BigDecimal.ZERO);
            }

            userFundsRepository.save(fund);
        }
    }


    @Transactional
    public void cancelOrderUserFunds(User user, BigDecimal amount) {
        List<UserFunds> userFundsList = userFundsRepository.findByUserId(user.getId());

        BigDecimal remainingAmount = amount;


        for (UserFunds userFunds : userFundsList) {
            if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal pendingAmount = userFunds.getPendingAmount();
            BigDecimal transferAmount = remainingAmount.min(pendingAmount);

            userFunds.setPendingAmount(pendingAmount.subtract(transferAmount));
            userFunds.setAvailableAmount(userFunds.getAvailableAmount().add(transferAmount));

            userFundsRepository.save(userFunds);

            remainingAmount = remainingAmount.subtract(transferAmount);
        }
    }

    @Transactional
    public void addFundsAfterSale(User user, BigDecimal quantity, BigDecimal price) {
        BigDecimal totalAmountUSD = quantity.multiply(price);
        List<UserFunds> userFundsList = userFundsRepository.findByUserId(user.getId());
        UserFunds usdFunds = userFundsList.stream()
                .filter(fund -> fund.getCurrency() == CurrencyCode.USD)
                .findFirst()
                .orElseGet(() -> createNewUserFunds(user, CurrencyCode.USD));

        usdFunds.setAvailableAmount(usdFunds.getAvailableAmount().add(totalAmountUSD));
        usdFunds.setTotalAmount(usdFunds.getTotalAmount().add(totalAmountUSD));
        userFundsRepository.save(usdFunds);
    }

    public BigDecimal getTotalUSD(List<UserFunds> funds) {
        BigDecimal totalUSD = BigDecimal.ZERO;
        for (UserFunds fund : funds) {
            totalUSD = totalUSD.add(currencyService.convertToUSD(fund.getCurrency(), fund.getTotalAmount()));
        }
        return totalUSD;
    }
}