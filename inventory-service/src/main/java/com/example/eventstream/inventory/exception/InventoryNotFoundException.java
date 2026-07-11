package com.example.eventstream.inventory.exception;

public class InventoryNotFoundException extends RuntimeException{
    public InventoryNotFoundException(Long prductId){
        super("Inventory not found for product:" + prductId);
    }
}
