package org.example.spring1.userAssets;

import lombok.RequiredArgsConstructor;
import org.example.spring1.asset.model.Asset;
import org.example.spring1.asset.model.dto.AssetDTO;
import org.example.spring1.exception.EntityNotFoundException;
import org.example.spring1.exception.InsufficientAssetsException;
import org.example.spring1.user.UserDetailsImpl;
import org.example.spring1.user.model.User;
import org.example.spring1.userAssets.model.UserAssets;

import org.example.spring1.userAssets.model.dto.UserAssetDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserAssetsService {

    private final UserAssetsRepository userAssetsRepository;

    public List<UserAssetDTO> getUserAssetDTOs(Long userId) {
        List<UserAssets> userAssets = userAssetsRepository.findByUserId(userId);
        return userAssets.stream().map(this::convertToUserAssetDTO).collect(Collectors.toList());
    }

    private UserAssetDTO convertToUserAssetDTO(UserAssets userAssets) {
        Asset asset = userAssets.getAsset();
        AssetDTO assetDTO = AssetDTO.builder()
                .name(asset.getName())
                .symbol(asset.getSymbol())
                .currentPrice(asset.getCurrentPrice())
                .assetType(asset.getAssetType().name())
                .build();

        return UserAssetDTO.builder()
                .asset(assetDTO)
                .quantity(userAssets.getQuantity())
                .build();
    }

    public void updateUserAssets(User user, Asset asset, BigDecimal quantity, boolean isBuy) {
        UserAssets userAssets = userAssetsRepository.findByUserIdAndAssetSymbol(user.getId(), asset.getSymbol())
                .orElse(UserAssets.builder()
                        .user(user)
                        .asset(asset)
                        .quantity(BigDecimal.ZERO)
                        .build());

        if (isBuy) {
            userAssets.setQuantity(userAssets.getQuantity().add(quantity));
        } else {
            userAssets.setQuantity(userAssets.getQuantity().subtract(quantity));
            if (userAssets.getQuantity().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Cannot have negative quantity of assets.");
            }
        }

        userAssetsRepository.save(userAssets);
    }

    public void deductAssets(User user, String assetSymbol, BigDecimal quantity) {
        UserAssets userAssets = userAssetsRepository.findByUserIdAndAssetSymbol(user.getId(), assetSymbol)
                .orElseThrow(() -> new EntityNotFoundException("User assets not found for the specified asset"));

        BigDecimal currentQuantity = userAssets.getQuantity();
        if (currentQuantity.compareTo(quantity) < 0) {
            throw new InsufficientAssetsException("User does not have enough of the asset to sell");
        }

        userAssets.setQuantity(currentQuantity.subtract(quantity));
        userAssetsRepository.save(userAssets);
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).getId();
    }
}