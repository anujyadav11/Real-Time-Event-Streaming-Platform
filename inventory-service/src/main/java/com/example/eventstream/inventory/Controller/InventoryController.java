package com.example.eventstream.inventory.Controller;


import com.example.eventstream.inventory.dto.request.ConfirmInventoryRequest;
import com.example.eventstream.inventory.dto.request.ReleaseInventoryRequest;
import com.example.eventstream.inventory.dto.request.ReserveInventoryRequest;
import com.example.eventstream.inventory.dto.response.InventoryResponse;
import com.example.eventstream.inventory.entity.Inventory;
import com.example.eventstream.inventory.mapper.InventoryMapper;
import com.example.eventstream.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory", description = "Inventory availability and reservation APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class InventoryController {
    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;

    public InventoryController(InventoryService inventoryService, InventoryMapper inventoryMapper){
        this.inventoryService = inventoryService;
        this.inventoryMapper = inventoryMapper;
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get inventory", description = "Returns inventory for a product.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory returned"),
            @ApiResponse(responseCode = "404", description = "Product inventory not found")
    })
    public InventoryResponse getInventory(@PathVariable Long productId){
        Inventory inventory = inventoryService.getInventory(productId);
        return inventoryMapper.toResponse(inventory);
    }

    @PostMapping("/reserve")
    @Operation(summary = "Reserve inventory", description = "Reserves a product quantity for an order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory reserved"),
            @ApiResponse(responseCode = "400", description = "Invalid reservation or insufficient stock")
    })
    public InventoryResponse reserveInventory(@Valid @RequestBody ReserveInventoryRequest request){
        Inventory inventory = inventoryService.reserveInventory(
                request.productId(),
                request.quantity()
        );
        return inventoryMapper.toResponse(inventory);
    }
    @PostMapping("/release")
    @Operation(summary = "Release inventory", description = "Releases a previously reserved product quantity.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory released"),
            @ApiResponse(responseCode = "400", description = "Invalid release request")
    })
    public InventoryResponse releaseInventory(@Valid @RequestBody ReleaseInventoryRequest request){
        Inventory inventory = inventoryService.releaseInventory(
                request.productId(),
                request.quantity()
        );
        return inventoryMapper.toResponse(inventory);
    }
    @PostMapping("/confirm")
    @Operation(summary = "Confirm inventory", description = "Confirms a reserved product quantity.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory confirmed"),
            @ApiResponse(responseCode = "400", description = "Invalid confirmation request")
    })
    public InventoryResponse confirmInventory(@Valid @RequestBody ConfirmInventoryRequest request){
        Inventory inventory = inventoryService.confirmInventory(
                request.productId(),
                request.quantity()
        );
        return inventoryMapper.toResponse(inventory);
    }
}
