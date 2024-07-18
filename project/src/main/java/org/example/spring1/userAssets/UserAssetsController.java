package org.example.spring1.userAssets;

import com.fasterxml.jackson.databind.util.JSONWrappedObject;
import lombok.RequiredArgsConstructor;
import org.example.spring1.asset.model.Asset;
import org.example.spring1.user.model.User;
import org.example.spring1.userAssets.model.UserAssets;
import org.example.spring1.userAssets.model.dto.DeductAssetsDTO;
import org.example.spring1.userAssets.model.dto.UpdateUserAssetsDTO;
import org.example.spring1.userAssets.model.dto.UserAssetDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-assets")
@RequiredArgsConstructor
public class UserAssetsController {

    private final UserAssetsService userAssetsService;

    @GetMapping
    public ResponseEntity<List<UserAssetDTO>> getUserAssets() {
        Long userId = userAssetsService.getCurrentUserId();
        List<UserAssetDTO> userAssets = userAssetsService.getUserAssetDTOs(userId);
        return ResponseEntity.ok(userAssets);
    }
}