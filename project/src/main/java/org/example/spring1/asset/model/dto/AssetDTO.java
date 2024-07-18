package org.example.spring1.asset.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetDTO {
    private String name;
    private String symbol;
    private BigDecimal currentPrice;
    private String assetType;
    private Long id;
}
