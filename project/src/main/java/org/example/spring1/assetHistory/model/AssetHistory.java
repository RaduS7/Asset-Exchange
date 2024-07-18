package org.example.spring1.assetHistory.model;

import lombok.*;
import jakarta.persistence.*;
import org.example.spring1.asset.model.Asset;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AssetHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;
}