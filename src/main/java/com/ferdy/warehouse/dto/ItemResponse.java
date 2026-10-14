package com.ferdy.warehouse.dto;

import com.ferdy.warehouse.entity.Item;

import java.math.BigDecimal;
import java.util.List;

public record ItemResponse(Long id, String name, String description, BigDecimal price, int stock,
                           List<VariantResponse> variants) {

    public static ItemResponse from(Item item) {
        return new ItemResponse(item.getId(), item.getName(), item.getDescription(), item.getPrice(),
                item.getStock(), item.getVariants().stream().map(VariantResponse::from).toList());
    }
}
