package org.example.spring1.assetHistory;

import lombok.RequiredArgsConstructor;
import org.example.spring1.assetHistory.model.AssetHistory;
import org.example.spring1.assetHistory.model.dto.AssetHistoryDTO;
import org.example.spring1.assetHistory.model.dto.AssetHistoryRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asset-history")
@RequiredArgsConstructor
public class AssetHistoryController {

    private final AssetHistoryService assetHistoryService;

    @PostMapping("/by-symbol")
    public ResponseEntity<List<AssetHistoryDTO>> getAssetHistoryBySymbol(@RequestBody AssetHistoryRequestDTO request) {
        List<AssetHistoryDTO> assetHistoryList = assetHistoryService.getAssetHistoryBySymbol(request.getSymbol());
        return ResponseEntity.ok(assetHistoryList);
    }
}