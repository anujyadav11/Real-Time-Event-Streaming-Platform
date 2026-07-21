package com.example.eventstream.pricingservice.controller;



import com.example.eventstream.common.dto.ProductPriceResponse;
import com.example.eventstream.pricingservice.service.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
@Tag(name = "Pricing", description = "Product pricing APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @GetMapping("/products/{productId}")
    @Operation(summary = "Get product price", description = "Returns the current price for a product.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product price returned"),
            @ApiResponse(responseCode = "404", description = "Product price not found")
    })
    public ProductPriceResponse getPrice(
            @PathVariable Long productId) {

        return pricingService.getPrice(productId);
    }
}
