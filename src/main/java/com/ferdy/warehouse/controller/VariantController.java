package com.ferdy.warehouse.controller;

import com.ferdy.warehouse.dto.VariantRequest;
import com.ferdy.warehouse.dto.VariantResponse;
import com.ferdy.warehouse.service.VariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VariantController {

    private final VariantService variantService;

    @GetMapping("/api/items/{itemId}/variants")
    public List<VariantResponse> findByItem(@PathVariable Long itemId) {
        return variantService.findByItem(itemId);
    }

    @PostMapping("/api/items/{itemId}/variants")
    @ResponseStatus(HttpStatus.CREATED)
    public VariantResponse create(@PathVariable Long itemId, @Valid @RequestBody VariantRequest request) {
        return variantService.create(itemId, request);
    }

    @GetMapping("/api/variants/{id}")
    public VariantResponse findById(@PathVariable Long id) {
        return variantService.findById(id);
    }

    @PutMapping("/api/variants/{id}")
    public VariantResponse update(@PathVariable Long id, @Valid @RequestBody VariantRequest request) {
        return variantService.update(id, request);
    }

    @DeleteMapping("/api/variants/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        variantService.delete(id);
    }
}
