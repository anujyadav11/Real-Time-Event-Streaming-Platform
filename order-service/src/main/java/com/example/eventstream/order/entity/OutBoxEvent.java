package com.example.eventstream.order.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox-events")
public class OutBoxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID aggregatedId;

    @Column(nullable = false)
    private String aggregateType;
    
    @Column(nullable = false)
    private String eventType;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Column(nullable = false)
    private boolean published = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;

    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
    }

}
