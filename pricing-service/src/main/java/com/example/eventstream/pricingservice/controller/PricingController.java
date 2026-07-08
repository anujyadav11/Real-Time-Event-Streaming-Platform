package com.example.eventstream.pricingservice.controller;



import com.example.eventstream.common.dto.ProductPriceResponse;
import com.example.eventstream.pricingservice.service.PricingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @GetMapping("/products/{productId}")
    public ProductPriceResponse getPrice(
            @PathVariable Long productId) {

        return pricingService.getPrice(productId);
    }
}