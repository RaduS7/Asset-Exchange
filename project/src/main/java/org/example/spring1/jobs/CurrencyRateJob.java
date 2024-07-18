package org.example.spring1.jobs;

import org.example.spring1.currency.CurrencyRepository;
import org.example.spring1.currency.model.Currency;
import org.example.spring1.currency.model.CurrencyCode;
import org.example.spring1.twelveData.TwelveDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CurrencyRateJob {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyRateJob.class);

    private final CurrencyRepository currencyRepository;
    private final TwelveDataService twelveDataService;

    @Scheduled(fixedRate = 60000)  // Every 1 minute
    public void updateAssetPrices() {
        List<Currency> currencies = currencyRepository.findAll();

        Flux.fromIterable(currencies)
                .delayElements(Duration.ofMillis(300)) // Delay of 0.3 seconds between each element
                .flatMap(currency -> {
                    if (currency.getCurrencyCode().equals(CurrencyCode.USD)) {
                        // Directly set the rate for USD/USD to 1.00
                        currency.setRateToUsd(BigDecimal.ONE);
                        return Mono.just(currency);
                    } else {
                        // Make an API call for other currencies
                        return twelveDataService.findStockPrice(currency.getCurrencyCode() + "/USD")
                                .map(price -> {
                                    currency.setRateToUsd(BigDecimal.valueOf(price));
                                    return currency;
                                })
                                .onErrorResume(error -> {
                                    logger.error("Failed to update price for {}: {}", currency.getCurrencyCode() + "/USD", error.getMessage());
                                    return Mono.empty(); // Continue with other currencies even if one fails
                                });
                    }
                })
                .subscribe(asset -> {
                    currencyRepository.save(asset);
                });
    }
}
