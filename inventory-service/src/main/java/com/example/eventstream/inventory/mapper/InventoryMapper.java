package com.example.eventstream.inventory.mapper;

import com.example.eventstream.inventory.dto.response.InventoryResponse;
import com.example.eventstream.inventory.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {
    public InventoryResponse toResponse(Inventory inventory){
        return new InventoryResponse(
                inventory.getProductId(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity()
        );
    }
}
