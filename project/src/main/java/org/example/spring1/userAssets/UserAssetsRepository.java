package org.example.spring1.userAssets;

import org.example.spring1.asset.model.Asset;
import org.example.spring1.user.model.User;
import org.example.spring1.userAssets.model.UserAssets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAssetsRepository extends JpaRepository<UserAssets, Long> {
    List<UserAssets> findByUserId(Long userId);
    Optional<UserAssets> findByIdAndUserId(Long assetId, Long userId);
    Optional<UserAssets> findByUserIdAndAssetSymbol(Long assetId, String assetSymbol);
}