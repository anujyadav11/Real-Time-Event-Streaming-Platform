package com.example.eventstream.inventory.repository;

import com.example.eventstream.inventory.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory , UUID> {
    Optional<Inventory> findByProductId(Long productId);

    /**
     * Locks one product row while its stock counters are being changed.  Without
     * this, two concurrent reservations can both read the same available count
     * and oversell the product.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select inventory from Inventory inventory where inventory.productId = :productId")
    Optional<Inventory> findByProductIdForUpdate(@Param("productId") Long productId);
}
