package com.example.eventstream.inventory.Controller;


import com.example.eventstream.inventory.dto.request.ConfirmInventoryRequest;
import com.example.eventstream.inventory.dto.request.ReleaseInventoryRequest;
import com.example.eventstream.inventory.dto.request.ReserveInventoryRequest;
import com.example.eventstream.inventory.dto.response.InventoryResponse;
import com.example.eventstream.inventory.entity.Inventory;
import com.example.eventstream.inventory.mapper.InventoryMapper;
import com.example.eventstream.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;

    public InventoryController(InventoryService inventoryService, InventoryMapper inventoryMapper){
        this.inventoryService = inventoryService;
        this.inventoryMapper = inventoryMapper;
    }

    @GetMapping("/{productId}")
    public InventoryResponse getInventory(@PathVariable Long productId){
        Inventory inventory = inventoryService.getInventory(productId);
        return inventoryMapper.toResponse(inventory);
    }

    @PostMapping("/reserve")
    public InventoryResponse reserveInventory(@Valid @RequestBody ReserveInventoryRequest request){
        Inventory inventory = inventoryService.reserveInventory(
                request.productId(),
                request.quantity()
        );
        return inventoryMapper.toResponse(inventory);
    }
    @PostMapping("/release")
    public InventoryResponse releaseInventory(@Valid @RequestBody ReleaseInventoryRequest request){
        Inventory inventory = inventoryService.releaseInventory(
                request.productId(),
                request.quantity()
        );
        return inventoryMapper.toResponse(inventory);
    }
    @PostMapping("/confirm")
    public InventoryResponse confirmInventory(@Valid @RequestBody ConfirmInventoryRequest request){
        Inventory inventory = inventoryService.confirmInventory(
                request.productId(),
                request.quantity()
        );
        return inventoryMapper.toResponse(inventory);
    }
}
