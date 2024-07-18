package org.example.spring1.tradeOrder;

import org.example.spring1.tradeOrder.model.TradeOrder;
import org.example.spring1.tradeOrder.model.dto.TradeOrderDTO;
import org.example.spring1.tradeOrder.model.dto.TradeOrderUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TradeOrderMapper {
    TradeOrderDTO toTradeOrderDto(TradeOrder tradeOrder);

    @Mapping(target = "user", ignore = true)
    TradeOrder toEntity(TradeOrderDTO tradeOrderDTO);

    void updateEntityFromDto(TradeOrderUpdateDTO tradeOrderDTO, @MappingTarget TradeOrder tradeOrder);
}