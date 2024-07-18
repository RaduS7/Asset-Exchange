package org.example.spring1.asset;

import org.example.spring1.asset.model.Asset;
import org.example.spring1.asset.model.AssetType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AssetRepositoryTest {

    @Autowired
    private AssetRepository assetRepository;

    @AfterEach
    void tearDown() {
        assetRepository.deleteAll();
    }

    @Test
    void findBySymbol_ExistingSymbol_ReturnsAsset() {
        Asset asset =  new Asset(null, "Bitcoin", "BTC/USD", new BigDecimal("60000.00"), AssetType.CRYPTO);
        assetRepository.save(asset);

        Optional<Asset> optionalAsset = assetRepository.findBySymbol("BTC/USD");

        assertTrue(optionalAsset.isPresent());
        assertEquals(asset.getSymbol(), optionalAsset.get().getSymbol());
    }

    @Test
    void findBySymbol_NonExistingSymbol_ReturnsEmpty() {
        Optional<Asset> optionalAsset = assetRepository.findBySymbol("ETH/USD");

        assertTrue(optionalAsset.isEmpty());
    }
}