package com.food.ordering.system.order.service.messaging.listener.kafka;

import static com.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGES_DELIMITER;

import com.food.ordering.system.kafka.consumer.service.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResponseKafkaListener implements KafkaConsumer<PaymentResponseAvroModel> {

    private final PaymentResponseMessageListener paymentResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    @Override
    @KafkaListener(
            id = "${kafka-consumer-config.payment-consumer-group-id}",
            topics = "${order-service.payment-response-topic-name}"
    )
    public void receive(@Payload List<PaymentResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("[{}] number of payment responses received with keys [{}] partitions [{}] and offsets [{}]",
                 messages.size(), keys, partitions, offsets);

        messages.forEach(paymentResponseAvroModel -> {
            if (PaymentStatus.COMPLETED.equals(paymentResponseAvroModel.getPaymentStatus())) {
                log.info("Processing successful payment for order id [{}]", paymentResponseAvroModel.getOrderId());
                paymentResponseMessageListener.paymentCompleted(
                        orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel));
            } else if (PaymentStatus.CANCELLED.equals(paymentResponseAvroModel.getPaymentStatus()) ||
                    PaymentStatus.FAILED.equals(paymentResponseAvroModel.getPaymentStatus())) {
                log.info("Processing unsuccessful payment for order id [{}] with failure messages [{}]",
                         paymentResponseAvroModel.getOrderId(),
                         String.join(FAILURE_MESSAGES_DELIMITER, paymentResponseAvroModel.getFailureMessages()));
                paymentResponseMessageListener.paymentCancelled(
                        orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel));
            }
        });
    }
}
