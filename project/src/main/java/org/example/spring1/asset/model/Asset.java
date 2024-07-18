package org.example.spring1.asset.model;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @Column(length = 10, nullable = false, unique = true)
    private String symbol;

    @Column(nullable = false)
    private BigDecimal currentPrice;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private AssetType assetType;
}
