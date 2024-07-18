package org.example.spring1.jobs;

import org.example.spring1.asset.AssetRepository;
import org.example.spring1.asset.model.Asset;
import org.example.spring1.assetHistory.model.AssetHistory;
import org.example.spring1.assetHistory.AssetHistoryRepository;
import org.example.spring1.twelveData.TwelveDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class AssetPriceJob {

    private static final Logger logger = LoggerFactory.getLogger(AssetPriceJob.class);

    private final AssetRepository assetRepository;
    private final TwelveDataService twelveDataService;
    private final AssetHistoryRepository assetHistoryRepository;

    @Scheduled(fixedRate = 60000)  // Every 1 minute
    public void updateAssetPrices() {
        List<Asset> assets = assetRepository.findAll();

        Flux.fromIterable(assets)
                .delayElements(Duration.ofMillis(200)) // Delay of 0.2 seconds between each element
                .flatMap(asset ->
                        twelveDataService.findStockPrice(asset.getSymbol())
                                .map(price -> {
                                    BigDecimal newPrice = BigDecimal.valueOf(price);
                                    asset.setCurrentPrice(newPrice);

                                    AssetHistory assetHistory = AssetHistory.builder()
                                            .asset(asset)
                                            .time(LocalDateTime.now())
                                            .price(newPrice)
                                            .build();

                                    assetHistoryRepository.save(assetHistory);

                                    return asset;
                                })
                                .onErrorResume(error -> {
                                    logger.error("Failed to update price for {}: {}", asset.getSymbol(), error.getMessage());
                                    return Mono.empty(); // Continue with other assets even if one fails
                                })
                )
                .subscribe(asset -> {
                    assetRepository.save(asset);
                });
    }
}
