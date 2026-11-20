package com.example.orderservice.kafka;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.orderservice.exception.OrderPublishException;
import com.example.orderservice.model.OrderCreatedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderEventPublisher {


	private static final long SEND_TIMEOUT_SECONDS = 5;

	private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

	private final String topic;

	public OrderEventPublisher(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
			@Value("${app.kafka.topics.order-created}") String topic) {
		this.kafkaTemplate = kafkaTemplate;
		this.topic = topic;
	}

	public void publish(OrderCreatedEvent event) {
		String orderId = event.getOrderId().toString();
		try {
			RecordMetadata metadata = kafkaTemplate.send(topic, orderId, event)
				.get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS)
				.getRecordMetadata();
			log.info("Published order {} to {}-{} at offset {}", orderId, metadata.topic(), metadata.partition(),
					metadata.offset());
		}
		catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw publishFailed(orderId, ex);
		}
		catch (ExecutionException ex) {
			throw publishFailed(orderId, ex.getCause());
		}
		catch (TimeoutException | KafkaException ex) {
			throw publishFailed(orderId, ex);
		}
	}

	private OrderPublishException publishFailed(String orderId, Throwable cause) {
		log.error("Failed to publish order {} to topic {}", orderId, topic, cause);
		return new OrderPublishException("Failed to publish order " + orderId, cause);
	}

}
