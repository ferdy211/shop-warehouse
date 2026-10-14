package com.ferdy.warehouse;

import com.ferdy.warehouse.dto.ItemRequest;
import com.ferdy.warehouse.dto.ItemResponse;
import com.ferdy.warehouse.dto.SaleRequest;
import com.ferdy.warehouse.dto.SaleResponse;
import com.ferdy.warehouse.dto.VariantRequest;
import com.ferdy.warehouse.dto.VariantResponse;
import com.ferdy.warehouse.exception.BadRequestException;
import com.ferdy.warehouse.exception.InsufficientStockException;
import com.ferdy.warehouse.service.ItemService;
import com.ferdy.warehouse.service.SaleService;
import com.ferdy.warehouse.service.VariantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class SaleServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private VariantService variantService;

    @Autowired
    private SaleService saleService;

    @Test
    void sellItemDecreasesStock() {
        ItemResponse item = itemService.create(new ItemRequest("Mug", null, new BigDecimal("20000"), 5));

        SaleResponse sale = saleService.sell(new SaleRequest(item.id(), null, 2));

        assertEquals(3, sale.remainingStock());
        assertEquals(0, new BigDecimal("40000").compareTo(sale.totalPrice()));
    }

    @Test
    void sellItemBeyondStockIsRejected() {
        ItemResponse item = itemService.create(new ItemRequest("Mug", null, new BigDecimal("20000"), 1));

        assertThrows(InsufficientStockException.class,
                () -> saleService.sell(new SaleRequest(item.id(), null, 2)));
        assertEquals(1, itemService.findById(item.id()).stock());
    }

    @Test
    void sellVariantDecreasesVariantStock() {
        ItemResponse item = itemService.create(new ItemRequest("Shirt", null, new BigDecimal("100000"), 0));
        VariantResponse variant = variantService.create(item.id(),
                new VariantRequest("Red - M", new BigDecimal("120000"), 4));

        SaleResponse sale = saleService.sell(new SaleRequest(item.id(), variant.id(), 4));

        assertEquals(0, sale.remainingStock());
        assertThrows(InsufficientStockException.class,
                () -> saleService.sell(new SaleRequest(item.id(), variant.id(), 1)));
    }

    @Test
    void sellItemWithVariantsRequiresVariantId() {
        ItemResponse item = itemService.create(new ItemRequest("Shirt", null, new BigDecimal("100000"), 10));
        variantService.create(item.id(), new VariantRequest("Red - M", new BigDecimal("120000"), 4));

        assertThrows(BadRequestException.class,
                () -> saleService.sell(new SaleRequest(item.id(), null, 1)));
    }
}
