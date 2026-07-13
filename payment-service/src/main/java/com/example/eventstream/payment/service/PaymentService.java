package com.example.eventstream.payment.service;

import com.example.eventstream.common.command.ProcessPaymentCommand;
import com.example.eventstream.common.event.InventoryReservedEvent;
import com.example.eventstream.common.event.PaymentCompletedEvent;
import com.example.eventstream.common.event.PaymentFailedEvent;
import com.example.eventstream.payment.entity.Payment;
import com.example.eventstream.common.enums.PaymentStatus;
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
    public void processPayment(ProcessPaymentCommand command) {
        log.info("[{}] Processing payment for order: {}", command.correlationId(), command.orderId());
        try{
            boolean paymentSuccessful = true;
            if(!paymentSuccessful){
                PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                        UUID.randomUUID(),
                        command.orderId(),
                        command.productId(),
                        command.quantity(),
                        "Payment declined",
                        command.correlationId()
                );
                paymentEventProducer.publishFailed(failedEvent).join();
                log.warn("Payment failed for order {}", command.orderId());
                return;
            }
        Payment payment = new Payment();

        payment.setOrderId(command.orderId());
        payment.setAmount(command.amount());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);

        log.info("Payment saved successfully for order: {}",
                command.orderId());

        PaymentCompletedEvent paymentCompletedEvent =
                new PaymentCompletedEvent(
                        UUID.randomUUID(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        true,
                        payment.getPaidAt(),
                        command.correlationId()
                );

        paymentEventProducer.publishCompleted(paymentCompletedEvent);

        log.info("PaymentCompletedEvent published for order: {}",
                payment.getOrderId());
        } catch (Exception ex){
            log.error("Payment Processing failed for order: {}", command.orderId(), ex);
            throw  ex;
        }
    }
}
