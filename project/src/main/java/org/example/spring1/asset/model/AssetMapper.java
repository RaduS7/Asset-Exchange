package org.example.spring1.asset.model;

import org.example.spring1.asset.model.Asset;
import org.example.spring1.asset.model.dto.AssetDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssetMapper {

    AssetDTO toAssetDto(Asset asset);

    Asset toAsset(AssetDTO assetDTO);
}