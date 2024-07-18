package org.example.spring1.userAssets.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.spring1.asset.model.dto.AssetDTO;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAssetDTO {
    private AssetDTO asset;
    private BigDecimal quantity;
}
