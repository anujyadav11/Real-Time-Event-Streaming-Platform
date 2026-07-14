package com.example.eventstream.sagaorchestrator.kafka.producer;

import com.example.eventstream.common.command.*;
import com.example.eventstream.common.constants.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class SagaCommandProducer {
    private static final Logger log =
            LoggerFactory.getLogger(SagaCommandProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public SagaCommandProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public CompletableFuture<Void> publishReserveInventoryCommand(
            ReserveInventoryCommand command) {
        log.info("Publishing ReserveInventoryCommand for order {}",
                command.orderId());
        return kafkaTemplate.send(
                        KafkaTopics.RESERVE_INVENTORY_COMMAND,
                        command.orderId().toString(),
                        command)
                .thenAccept(result ->
                        log.info("ReserveInventoryCommand published"));

    }
    public CompletableFuture<Void> publishProcessPaymentCommand(
            ProcessPaymentCommand command) {
        log.info("Publishing ProcessPaymentCommand for order {}",
                command.orderId());
        return kafkaTemplate.send(
                        KafkaTopics.PROCESS_PAYMENT_COMMAND,
                        command.orderId().toString(),
                        command)
                .thenAccept(result ->
                        log.info("ProcessPaymentCommand published"));
    }
    public CompletableFuture<Void> publishReleaseInventoryCommand(
            ReleaseInventoryCommand command) {
        log.info("Publishing ReleaseInventoryCommand for order {}",
                command.orderId());
        return kafkaTemplate.send(
                        KafkaTopics.RELEASE_INVENTORY_COMMAND,
                        command.orderId().toString(),
                        command)
                .thenAccept(result ->
                        log.info("ReleaseInventoryCommand published"));
    }
    public CompletableFuture<Void> publishCreateDeliveryCommand(
            CreateDeliveryCommand command) {
        log.info("Publishing CreateDeliveryCommand for order {}",
                command.orderId());
        return kafkaTemplate.send(
                        KafkaTopics.CREATE_DELIVERY_COMMAND,
                        command.orderId().toString(),
                        command)
                .thenAccept(result ->
                        log.info("CreateDeliveryCommand published"));
    }
    public CompletableFuture<Void> publishSendNotificationCommand(
            SendNotificationCommand command) {
        log.info("Publishing SendNotificationCommand for order {}",
                command.orderId());
        return kafkaTemplate.send(
                        KafkaTopics.SEND_NOTIFICATION_COMMAND,
                        command.orderId().toString(),
                        command)
                .thenAccept(result ->
                        log.info("SendNotificationCommand published"));
    }
    public CompletableFuture<Void> publishCancelOrderCommand(
            CancelOrderCommand command) {
        log.info("Publishing CancelOrderCommand for order {}",
                command.orderId());
        return kafkaTemplate.send(
                        KafkaTopics.CANCEL_ORDER_COMMAND,
                        command.orderId().toString(),
                        command)
                .thenAccept(result ->
                        log.info("CancelOrderCommand published"));
    }
}
