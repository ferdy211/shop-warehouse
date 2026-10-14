package com.ferdy.warehouse.dto;

import com.ferdy.warehouse.entity.Variant;

import java.math.BigDecimal;

public record VariantResponse(Long id, Long itemId, String name, BigDecimal price, int stock) {

    public static VariantResponse from(Variant variant) {
        return new VariantResponse(variant.getId(), variant.getItem().getId(), variant.getName(),
                variant.getPrice(), variant.getStock());
    }
}
