package org.example.spring1.userAsset;

import org.example.spring1.asset.model.Asset;
import org.example.spring1.asset.model.AssetType;
import org.example.spring1.user.model.User;
import org.example.spring1.userAssets.UserAssetsRepository;
import org.example.spring1.userAssets.model.UserAssets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserAssetsRepositoryTest {

    @Autowired
    private UserAssetsRepository userAssetsRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Asset asset;

    @BeforeEach
    void setup() {
        user = User.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("password123")
                .build();
        user = entityManager.persist(user);

        asset = Asset.builder()
                .name("Gold")
                .symbol("XAU")
                .assetType(AssetType.COMMODITY)
                .currentPrice(new BigDecimal("1800"))
                .build();
        asset = entityManager.persist(asset);
    }

    @AfterEach
    void tearDown() {
        userAssetsRepository.deleteAll();
        entityManager.flush();
    }

    @Test
    void findByUserId_WhenUserExists_ReturnsUserAssets() {
        UserAssets userAsset = UserAssets.builder()
                .user(user)
                .asset(asset)
                .quantity(new BigDecimal("2.0000"))
                .build();
        entityManager.persist(userAsset);

        List<UserAssets> foundAssets = userAssetsRepository.findByUserId(user.getId());
        assertFalse(foundAssets.isEmpty());
        assertEquals(1, foundAssets.size());
        assertEquals(asset.getId(), foundAssets.get(0).getAsset().getId());
    }

    @Test
    void findByIdAndUserId_WhenRecordExists_ReturnsUserAsset() {
        UserAssets userAsset = UserAssets.builder()
                .user(user)
                .asset(asset)
                .quantity(new BigDecimal("3.5000"))
                .build();
        entityManager.persist(userAsset);

        Optional<UserAssets> foundAsset = userAssetsRepository.findByIdAndUserId(userAsset.getId(), user.getId());
        assertTrue(foundAsset.isPresent());
        assertEquals(userAsset.getQuantity(), foundAsset.get().getQuantity());
    }

    @Test
    void findByUserIdAndAssetSymbol_WhenMatching_ReturnsUserAsset() {
        UserAssets userAsset = UserAssets.builder()
                .user(user)
                .asset(asset)
                .quantity(new BigDecimal("1.2500"))
                .build();
        entityManager.persist(userAsset);

        Optional<UserAssets> foundAsset = userAssetsRepository.findByUserIdAndAssetSymbol(user.getId(), asset.getSymbol());
        assertTrue(foundAsset.isPresent());
        assertEquals(userAsset.getQuantity(), foundAsset.get().getQuantity());
    }

    @Test
    void findByUserId_WhenUserDoesNotExist_ReturnsEmptyList() {
        List<UserAssets> foundAssets = userAssetsRepository.findByUserId(999L);
        assertTrue(foundAssets.isEmpty());
    }
}