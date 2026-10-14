package com.ferdy.warehouse.dto;

import java.math.BigDecimal;

public record SaleResponse(Long itemId, Long variantId, int quantity, BigDecimal unitPrice,
                           BigDecimal totalPrice, int remainingStock) {
}
