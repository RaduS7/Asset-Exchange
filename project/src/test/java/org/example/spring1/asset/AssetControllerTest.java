package org.example.spring1.asset;

import org.example.spring1.asset.model.dto.AssetDTO;
import org.example.spring1.security.JwtUtilsService;
import org.example.spring1.user.UserDetailsImplService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AssetController.class)
public class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssetService assetService;

    @MockBean
    private JwtUtilsService jwtUtilsService;

    @MockBean
    private UserDetailsImplService userDetailsImplService;

    private AssetDTO assetDTO;

    @BeforeEach
    void setUp() {
        assetDTO = AssetDTO.builder()
                .name("Bitcoin")
                .symbol("BTC/USD")
                .currentPrice(BigDecimal.valueOf(60000.0))
                .assetType("CRYPTO")
                .build();
    }

    @Test
    @WithMockUser
    public void getAllAssets_ReturnsListOfAssets() throws Exception {
        List<AssetDTO> assets = Arrays.asList(assetDTO);
        when(assetService.getAllAssets()).thenReturn(assets);

        mockMvc.perform(get("/api/assets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bitcoin"))
                .andExpect(jsonPath("$[0].symbol").value("BTC/USD"))
                .andExpect(jsonPath("$[0].currentPrice").value("60000.0"))
                .andExpect(jsonPath("$[0].assetType").value("CRYPTO"));
    }

    @Test
    @WithMockUser
    public void getAssetById_ExistingId_ReturnsAsset() throws Exception {
        when(assetService.getAssetById(1L)).thenReturn(assetDTO);

        mockMvc.perform(get("/api/assets/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bitcoin"))
                .andExpect(jsonPath("$.symbol").value("BTC/USD"))
                .andExpect(jsonPath("$.currentPrice").value("60000.0"))
                .andExpect(jsonPath("$.assetType").value("CRYPTO"));
    }
}

