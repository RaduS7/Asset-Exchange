package org.example.spring1.config;

import lombok.RequiredArgsConstructor;
import org.example.spring1.asset.AssetRepository;
import org.example.spring1.asset.model.Asset;
import org.example.spring1.asset.model.AssetType;
import org.example.spring1.currency.CurrencyRepository;
import org.example.spring1.currency.model.Currency;
import org.example.spring1.currency.model.CurrencyCode;
import org.example.spring1.user.RoleRepository;
import org.example.spring1.user.UserRepository;
import org.example.spring1.user.UserService;
import org.example.spring1.user.model.ERole;
import org.example.spring1.user.model.Role;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class Bootstrap {

  private final RoleRepository roleRepository;
  private final AssetRepository assetRepository;
  private final CurrencyRepository currencyRepository;
  private final UserService userService;

  @EventListener(ApplicationReadyEvent.class)
  public void bootstrapData() {
    for (ERole value : ERole.values()) {
      if (roleRepository.findByName(value).isEmpty()) {
        roleRepository.save(
            Role.builder().name(value).build()
        );
      }
    }

    userService.createAdminUser("radus", "radus@email.com", "password123");

    List<Asset> predefinedAssets = List.of(
            new Asset(null, "Gold", "XAU/USD", new BigDecimal("1800.00"), AssetType.COMMODITY),
            new Asset(null, "Apple Inc.", "AAPL", new BigDecimal("150.00"), AssetType.STOCK),
            new Asset(null, "Tesla Inc.", "TSLA", new BigDecimal("900.00"), AssetType.STOCK),
            new Asset(null, "Bitcoin", "BTC/USD", new BigDecimal("60000.00"), AssetType.CRYPTO)
    );

    for (Asset predefinedAsset : predefinedAssets) {
      if (assetRepository.findBySymbol(predefinedAsset.getSymbol()).isEmpty()) {
        assetRepository.save(predefinedAsset);
      }
    }

    List<Currency> currencies = List.of(
            new Currency(null, CurrencyCode.USD, BigDecimal.ONE),
            new Currency(null, CurrencyCode.EUR, new BigDecimal("1.18"))
//            new Currency(null, CurrencyCode.RON, new BigDecimal("0.24"))
    );

    currencies.forEach(currency -> {
      if (currencyRepository.findByCurrencyCode(currency.getCurrencyCode()).isEmpty()) {
        currencyRepository.save(currency);
      }
    });
  }
}
