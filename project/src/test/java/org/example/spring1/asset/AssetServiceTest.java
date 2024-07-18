package org.example.spring1.asset;

import org.example.spring1.asset.model.Asset;
import org.example.spring1.asset.model.AssetMapper;
import org.example.spring1.asset.model.AssetType;
import org.example.spring1.asset.model.dto.AssetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssetMapper assetMapper;

    @InjectMocks
    private AssetService assetService;

    private Asset asset;
    private AssetDTO assetDTO;

    @BeforeEach
    void setUp() {
        asset = new Asset(1L, "Bitcoin", "BTC/USD", new BigDecimal("60000.00"), AssetType.CRYPTO);
        assetDTO = AssetDTO.builder()
                .name("Bitcoin")
                .symbol("BTC/USD")
                .currentPrice(BigDecimal.valueOf(60000.0))
                .assetType("CRYPTO")
                .build();
    }

    @Test
    void getAllAssets_ReturnsAssetDTOList() {
        when(assetRepository.findAll()).thenReturn(Collections.singletonList(asset));
        List<AssetDTO> result = assetService.getAllAssets();
        assertEquals(1, result.size());
        verify(assetMapper).toAssetDto(asset);
    }

    @Test
    void getAssetById_NonExistingId_ThrowsException() {
        when(assetRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> assetService.getAssetById(1L));
    }

    @Test
    void updateAsset_NonExistingId_ThrowsException() {
        when(assetRepository.findById(anyLong())).thenReturn(Optional.empty());
        AssetDTO updatedDTO = AssetDTO.builder()
                .name("Bitcoin Updated")
                .symbol("BTC/USD")
                .currentPrice(BigDecimal.valueOf(65000.0))
                .assetType("CRYPTO")
                .build();
        ;
        assertThrows(IllegalArgumentException.class, () -> assetService.updateAsset(1L, updatedDTO));
    }
}
