package com.example.eventstream.inventory.Controller;

import com.example.eventstream.common.constants.SecurityHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    @PostMapping
    public ResponseEntity<Void> reserveInventory(
            @RequestHeader(SecurityHeaders.USER_ROLE) String role){
        if(!"ADMIN".equals(role)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok().build();
    }
}
