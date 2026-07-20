package com.example.eventstream.order.entity;

import com.example.eventstream.common.enums.OutBoxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutBoxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

   @Enumerated(EnumType.STRING)
   @Column(nullable = false)
   @Builder.Default
   private OutBoxStatus status =  OutBoxStatus.NEW;

   @Column(nullable = false)
   @Builder.Default
   private int retryCount = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;

    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
    }

}
