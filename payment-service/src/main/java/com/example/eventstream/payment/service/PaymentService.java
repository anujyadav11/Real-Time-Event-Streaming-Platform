package com.example.eventstream.payment.service;

import com.example.eventstream.common.event.InventoryReservedEvent;
import com.example.eventstream.common.event.PaymentCompletedEvent;
import com.example.eventstream.payment.entity.Payment;
import com.example.eventstream.payment.entity.PaymentStatus;
import com.example.eventstream.payment.kafka.producer.PaymentEventProducer;
import com.example.eventstream.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;

    public PaymentService(PaymentRepository paymentRepository,
                          PaymentEventProducer paymentEventProducer) {
        this.paymentRepository = paymentRepository;
        this.paymentEventProducer = paymentEventProducer;
    }

    @Transactional
    public void processPayment(InventoryReservedEvent event) {
        log.info("Processing payment for order: {}", event.orderId());

        Payment payment = new Payment();

        payment.setOrderId(event.orderId());

        payment.setAmount(java.math.BigDecimal.valueOf(299));

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);

        log.info("Payment saved successfully for order: {}",
                event.orderId());

        PaymentCompletedEvent paymentCompletedEvent =
                new PaymentCompletedEvent(
                        payment.getOrderId(),
                        payment.getAmount(),
                        true,
                        payment.getPaidAt()
                );

        paymentEventProducer.publish(paymentCompletedEvent);

        log.info("PaymentCompletedEvent published for order: {}",
                payment.getOrderId());
    }
}