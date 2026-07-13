package com.example.eventstream.payment.kafka.consumer;

import com.example.eventstream.common.command.ProcessPaymentCommand;
import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.payment.service.PaymentService;
import com.example.infrastructure.redis.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class ProcessPaymentCommandConsumer {
    private static final Logger log =
            LoggerFactory.getLogger(ProcessPaymentCommandConsumer.class);
    private static final String CONSUMER_NAME = "payment-command";
    private final PaymentService paymentService;
    private final IdempotencyService idempotencyService;

    public ProcessPaymentCommandConsumer(
            PaymentService paymentService,
            IdempotencyService idempotencyService) {
        this.paymentService = paymentService;
        this.idempotencyService = idempotencyService;
    }
    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(
                    delay = 2000,
                    multiplier = 2.0
            ),
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = KafkaTopics.PROCESS_PAYMENT_COMMAND,
            groupId = "payment-group"
    )
    public void consume(ProcessPaymentCommand command) {
        if (idempotencyService.isProcessed(
                CONSUMER_NAME,
                command.commandId())) {
            log.info(
                    "Duplicate ProcessPaymentCommand ignored for order {}",
                    command.orderId());
            return;
        }
        log.info(
                "Received ProcessPaymentCommand for order {}",
                command.orderId());
        paymentService.processPayment(command);
        idempotencyService.markProcessed(
                CONSUMER_NAME,
                command.commandId());
        log.info(
                "Payment processing completed for order {}",
                command.orderId());
    }
}