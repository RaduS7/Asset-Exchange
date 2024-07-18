package org.example.spring1.assetHistory;

import org.example.spring1.assetHistory.model.AssetHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long> {
    List<AssetHistory> findByAssetId(Long assetId);
}
