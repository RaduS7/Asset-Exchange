package org.example.spring1.userAssets.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserAssetsDTO {
    private String assetSymbol;
    private BigDecimal quantity;
    private boolean isBuy;
}
