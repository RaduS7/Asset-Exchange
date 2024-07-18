package org.example.spring1.assetHistory;

import lombok.RequiredArgsConstructor;
import org.example.spring1.asset.AssetRepository;
import org.example.spring1.assetHistory.model.AssetHistory;
import org.example.spring1.assetHistory.model.dto.AssetHistoryDTO;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetHistoryService {

    private final AssetRepository assetRepository;
    private final AssetHistoryRepository assetHistoryRepository;

    public List<AssetHistoryDTO> getAssetHistoryBySymbol(String symbol) {
        Long assetId = assetRepository.findBySymbol(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Asset with symbol " + symbol + " not found"))
                .getId();

        List<AssetHistory> assetHistoryList = assetHistoryRepository.findByAssetId(assetId);

        return assetHistoryList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AssetHistoryDTO convertToDTO(AssetHistory assetHistory) {
        return AssetHistoryDTO.builder()
                .time(assetHistory.getTime())
                .price(assetHistory.getPrice())
                .build();
    }
}