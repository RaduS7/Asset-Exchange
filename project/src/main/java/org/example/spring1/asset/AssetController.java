package org.example.spring1.asset;

import lombok.RequiredArgsConstructor;
import org.example.spring1.asset.model.dto.AssetDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AssetDTO> createAsset(@RequestBody AssetDTO assetDTO) {
        AssetDTO savedAssetDTO = assetService.saveAsset(assetDTO);
        return ResponseEntity.ok(savedAssetDTO);
    }

    @GetMapping
    public ResponseEntity<List<AssetDTO>> getAllAssets() {
        List<AssetDTO> assets = assetService.getAllAssets();
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetDTO> getAssetById(@PathVariable Long id) {
        AssetDTO assetDTO = assetService.getAssetById(id);
        return ResponseEntity.ok(assetDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AssetDTO> updateAsset(@PathVariable Long id, @RequestBody AssetDTO assetDTO) {
        AssetDTO updatedAssetDTO = assetService.updateAsset(id, assetDTO);
        return ResponseEntity.ok(updatedAssetDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/price/{symbol}")
    public ResponseEntity<BigDecimal> getCurrentPriceBySymbol(@PathVariable String symbol) {
        try {
            BigDecimal price = assetService.getCurrentPriceBySymbol(symbol);
            return ResponseEntity.ok(price);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
