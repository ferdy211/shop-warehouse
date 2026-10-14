package com.ferdy.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SaleRequest(
        @NotNull Long itemId,
        Long variantId,
        @NotNull @Min(1) Integer quantity) {
}
