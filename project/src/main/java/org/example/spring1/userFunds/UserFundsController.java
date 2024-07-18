package org.example.spring1.userFunds;

import lombok.RequiredArgsConstructor;
import org.example.spring1.currency.CurrencyService;
import org.example.spring1.userFunds.model.UserFunds;
import org.example.spring1.userFunds.model.dto.UserFundsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/user-funds")
@RequiredArgsConstructor
public class UserFundsController {

    private final UserFundsService userFundsService;
    private final CurrencyService currencyService;

    @PostMapping("/add")
    public ResponseEntity<UserFunds> addFunds(@RequestBody UserFundsDTO fundsOperation) {
        Long userId = userFundsService.getCurrentUserId();
        UserFunds updatedFunds = userFundsService.addOrInitializeFunds(userId, fundsOperation.getCurrency(), fundsOperation.getAmount());
        return ResponseEntity.ok(updatedFunds);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<UserFunds> withdrawFunds(@RequestBody UserFundsDTO fundsOperation) {
        Long userId = userFundsService.getCurrentUserId();
        UserFunds updatedFunds = userFundsService.withdrawFunds(userId, fundsOperation.getCurrency(), fundsOperation.getAmount());
        return ResponseEntity.ok(updatedFunds);
    }

    @GetMapping
    public ResponseEntity<List<UserFunds>> viewAllFunds() {
        Long userId = userFundsService.getCurrentUserId();
        List<UserFunds> funds = userFundsService.getUserFundsByUserId(userId);
        return ResponseEntity.ok(funds);
    }

    @GetMapping("/total-usd-value")
    public ResponseEntity<BigDecimal> getTotalUSDFundsValue() {
        Long userId = userFundsService.getCurrentUserId();
        List<UserFunds> funds = userFundsService.getUserFundsByUserId(userId);

        BigDecimal totalUSD = userFundsService.getTotalUSD(funds);

        return ResponseEntity.ok(totalUSD);
    }
}