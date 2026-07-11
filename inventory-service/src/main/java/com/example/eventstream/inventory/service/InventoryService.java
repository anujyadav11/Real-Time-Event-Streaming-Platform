package com.example.eventstream.inventory.service;

import com.example.eventstream.inventory.entity.Inventory;
import com.example.eventstream.inventory.exception.InsufficientInventoryException;
import com.example.eventstream.inventory.exception.InvalidInventoryOperationException;
import com.example.eventstream.inventory.exception.InventoryNotFoundException;
import com.example.eventstream.inventory.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository){
        this.inventoryRepository = inventoryRepository;
    }

    public Inventory getInventory(Long productId){
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));
    }

    public Inventory reserveInventory(Long productId, Integer quantity){
        Inventory inventory = getInventoryForUpdate(productId);

        if(inventory.getAvailableQuantity() < quantity){
            throw  new InsufficientInventoryException(productId);
        }
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);

        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);

        return inventoryRepository.save(inventory);
    }
    public Inventory releaseInventory(Long productId, Integer quantity){
        Inventory inventory = getInventoryForUpdate(productId);

        if(inventory.getReservedQuantity() < quantity){
            throw new InvalidInventoryOperationException("Cannot release more inventory than reserved");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);

        return inventoryRepository.save(inventory);
    }
    public Inventory confirmInventory(Long productId, Integer quantity){
        Inventory inventory = getInventoryForUpdate(productId);

        if(inventory.getReservedQuantity() < quantity){
            throw new InvalidInventoryOperationException("Cannot confirm more inventory than reserved");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);

        return inventoryRepository.save(inventory);
    }

    private Inventory getInventoryForUpdate(Long productId) {
        return inventoryRepository.findByProductIdForUpdate(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));
    }
}
