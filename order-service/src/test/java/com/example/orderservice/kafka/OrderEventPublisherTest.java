package com.example.orderservice.kafka;

import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.example.orderservice.model.OrderCreatedEvent;

@ExtendWith(MockitoExtension.class)
class OrderEventPublisherTest {

	private static final String TOPIC = "order-created";

	@Mock
	private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

	private OrderEventPublisher publisher;

	@BeforeEach
	void setUp() {
		publisher = new OrderEventPublisher(kafkaTemplate, TOPIC);
	}

	@Test
	void publishSendsEventToTopicKeyedByOrderId() {
		UUID orderId = UUID.randomUUID();
		OrderCreatedEvent event = new OrderCreatedEvent(orderId, 15L, "Laptop", 2, Instant.now());

		publisher.publish(event);

		verify(kafkaTemplate).send(TOPIC, orderId.toString(), event);
	}

}
