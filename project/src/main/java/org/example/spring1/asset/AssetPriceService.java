package org.example.spring1.asset;

import lombok.RequiredArgsConstructor;
import org.example.spring1.asset.model.Asset;
import org.example.spring1.asset.twelveData.TwelveDataService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetPriceService {

    private final AssetRepository assetRepository;
    private final TwelveDataService twelveDataService;

    @Scheduled(fixedRate = 60000)  // Every 1 minute
    public void updateAssetPrices() {
        List<Asset> assets = assetRepository.findAll();
        assets.forEach(asset -> {
            twelveDataService.findStockPrice(asset.getSymbol()).subscribe(price -> {
                asset.setCurrentPrice(BigDecimal.valueOf(price));
                assetRepository.save(asset);
            }, error -> {
                System.out.println("Failed to update price for " + asset.getSymbol() + ": " + error.getMessage());
            });
        });
    }
}