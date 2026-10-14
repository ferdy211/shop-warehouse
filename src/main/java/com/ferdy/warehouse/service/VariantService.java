package com.ferdy.warehouse.service;

import com.ferdy.warehouse.dto.VariantRequest;
import com.ferdy.warehouse.dto.VariantResponse;
import com.ferdy.warehouse.entity.Item;
import com.ferdy.warehouse.entity.Variant;
import com.ferdy.warehouse.exception.NotFoundException;
import com.ferdy.warehouse.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VariantService {

    private final VariantRepository variantRepository;
    private final ItemService itemService;

    @Transactional(readOnly = true)
    public List<VariantResponse> findByItem(Long itemId) {
        itemService.getItem(itemId);
        return variantRepository.findByItemId(itemId).stream().map(VariantResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public VariantResponse findById(Long id) {
        return VariantResponse.from(getVariant(id));
    }

    public VariantResponse create(Long itemId, VariantRequest request) {
        Item item = itemService.getItem(itemId);
        Variant variant = new Variant();
        variant.setItem(item);
        apply(variant, request);
        return VariantResponse.from(variantRepository.save(variant));
    }

    public VariantResponse update(Long id, VariantRequest request) {
        Variant variant = getVariant(id);
        apply(variant, request);
        return VariantResponse.from(variant);
    }

    public void delete(Long id) {
        Variant variant = getVariant(id);
        variant.getItem().getVariants().remove(variant);
        variantRepository.delete(variant);
    }

    private Variant getVariant(Long id) {
        return variantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Variant " + id + " not found"));
    }

    private void apply(Variant variant, VariantRequest request) {
        variant.setName(request.name());
        variant.setPrice(request.price());
        variant.setStock(request.stock());
    }
}
