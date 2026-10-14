package com.ferdy.warehouse.service;

import com.ferdy.warehouse.dto.SaleRequest;
import com.ferdy.warehouse.dto.SaleResponse;
import com.ferdy.warehouse.entity.Item;
import com.ferdy.warehouse.entity.Variant;
import com.ferdy.warehouse.exception.BadRequestException;
import com.ferdy.warehouse.exception.InsufficientStockException;
import com.ferdy.warehouse.exception.NotFoundException;
import com.ferdy.warehouse.repository.ItemRepository;
import com.ferdy.warehouse.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final ItemRepository itemRepository;
    private final VariantRepository variantRepository;

    @Transactional
    public SaleResponse sell(SaleRequest request) {
        Item item = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new NotFoundException("Item " + request.itemId() + " not found"));
        int quantity = request.quantity();

        if (request.variantId() == null) {
            return sellItem(item, quantity);
        }

        Variant variant = variantRepository.findByIdAndItemId(request.variantId(), item.getId())
                .orElseThrow(() -> new NotFoundException(
                        "Variant " + request.variantId() + " not found for item " + item.getId()));
        return sellVariant(item, variant, quantity);
    }

    private SaleResponse sellItem(Item item, int quantity) {
        if (variantRepository.existsByItemId(item.getId())) {
            throw new BadRequestException("Item " + item.getId() + " has variants, variantId is required");
        }
        BigDecimal unitPrice = item.getPrice();
        if (itemRepository.decreaseStock(item.getId(), quantity) == 0) {
            throw new InsufficientStockException("Insufficient stock for item " + item.getId());
        }
        int remaining = itemRepository.findById(item.getId()).orElseThrow().getStock();
        return response(item.getId(), null, quantity, unitPrice, remaining);
    }

    private SaleResponse sellVariant(Item item, Variant variant, int quantity) {
        BigDecimal unitPrice = variant.getPrice();
        if (variantRepository.decreaseStock(variant.getId(), quantity) == 0) {
            throw new InsufficientStockException("Insufficient stock for variant " + variant.getId());
        }
        int remaining = variantRepository.findById(variant.getId()).orElseThrow().getStock();
        return response(item.getId(), variant.getId(), quantity, unitPrice, remaining);
    }

    private SaleResponse response(Long itemId, Long variantId, int quantity, BigDecimal unitPrice, int remaining) {
        return new SaleResponse(itemId, variantId, quantity, unitPrice,
                unitPrice.multiply(BigDecimal.valueOf(quantity)), remaining);
    }
}
