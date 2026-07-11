package com.example.eventstream.inventory.service;

import com.example.eventstream.inventory.entity.Inventory;
import com.example.eventstream.inventory.exception.InsufficientInventoryException;
import com.example.eventstream.inventory.exception.InvalidInventoryOperationException;
import com.example.eventstream.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void reserveMovesStockFromAvailableToReservedUsingALock() {
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .availableQuantity(10)
                .reservedQuantity(2)
                .build();
        when(inventoryRepository.findByProductIdForUpdate(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Inventory result = inventoryService.reserveInventory(1L, 3);

        assertEquals(7, result.getAvailableQuantity());
        assertEquals(5, result.getReservedQuantity());
        verify(inventoryRepository).findByProductIdForUpdate(1L);
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void reserveDoesNotChangeStockWhenInventoryIsInsufficient() {
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .availableQuantity(2)
                .reservedQuantity(0)
                .build();
        when(inventoryRepository.findByProductIdForUpdate(1L)).thenReturn(Optional.of(inventory));

        assertThrows(InsufficientInventoryException.class,
                () -> inventoryService.reserveInventory(1L, 3));

        assertEquals(2, inventory.getAvailableQuantity());
        assertEquals(0, inventory.getReservedQuantity());
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void confirmRejectsQuantityGreaterThanReservedWithAccurateMessage() {
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .availableQuantity(2)
                .reservedQuantity(1)
                .build();
        when(inventoryRepository.findByProductIdForUpdate(eq(1L))).thenReturn(Optional.of(inventory));

        InvalidInventoryOperationException exception = assertThrows(
                InvalidInventoryOperationException.class,
                () -> inventoryService.confirmInventory(1L, 2));

        assertEquals("Cannot confirm more inventory than reserved", exception.getMessage());
        verify(inventoryRepository, never()).save(any());
    }
}
