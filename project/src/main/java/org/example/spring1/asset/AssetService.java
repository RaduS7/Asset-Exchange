package org.example.spring1.asset;

import lombok.RequiredArgsConstructor;
import org.example.spring1.asset.model.Asset;
import org.example.spring1.asset.model.AssetMapper;
import org.example.spring1.asset.model.AssetType;
import org.example.spring1.asset.model.dto.AssetDTO;
import org.example.spring1.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper; // Add mapper here

    public List<AssetDTO> getAllAssets() {
        return assetRepository.findAll().stream()
                .map(assetMapper::toAssetDto)
                .collect(Collectors.toList());
    }

    public AssetDTO getAssetById(Long id) {
        return assetRepository.findById(id)
                .map(assetMapper::toAssetDto)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
    }

    public AssetDTO saveAsset(AssetDTO assetDTO) {
        Asset asset = assetMapper.toAsset(assetDTO);
        asset = assetRepository.save(asset);
        return assetMapper.toAssetDto(asset);
    }

    public AssetDTO updateAsset(Long id, AssetDTO assetDTO) {
        return assetRepository.findById(id).map(existingAsset -> {
            existingAsset.setName(assetDTO.getName());
            existingAsset.setSymbol(assetDTO.getSymbol());
            existingAsset.setCurrentPrice(assetDTO.getCurrentPrice());
            existingAsset.setAssetType(AssetType.valueOf(assetDTO.getAssetType()));
            return assetMapper.toAssetDto(assetRepository.save(existingAsset));
        }).orElseThrow(() -> new IllegalArgumentException("Asset not found"));
    }

    public void deleteAsset(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
        assetRepository.delete(asset);
    }

    public BigDecimal getCurrentPriceBySymbol(String symbol) {
        Asset asset = assetRepository.findBySymbol(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found with symbol: " + symbol));
        return asset.getCurrentPrice();
    }

    public Asset getAssetBySymbol(String symbol) {
        return assetRepository.findBySymbol(symbol)
                .orElseThrow(() -> new EntityNotFoundException("Asset with symbol " + symbol + " not found"));
    }
}
