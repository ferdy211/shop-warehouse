package com.ferdy.warehouse.controller;

import com.ferdy.warehouse.dto.SaleRequest;
import com.ferdy.warehouse.dto.SaleResponse;
import com.ferdy.warehouse.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public SaleResponse sell(@Valid @RequestBody SaleRequest request) {
        return saleService.sell(request);
    }
}
