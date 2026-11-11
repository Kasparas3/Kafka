package com.example.orderservice.kafka;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import com.example.orderservice.exception.OrderPublishException;
import com.example.orderservice.model.OrderCreatedEvent;

@ExtendWith(MockitoExtension.class)
class OrderEventPublisherTest {

	private static final String TOPIC = "order-created";

	@Mock
	private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

	private OrderEventPublisher publisher;

	private OrderCreatedEvent event;

	private String key;

	@BeforeEach
	void setUp() {
		publisher = new OrderEventPublisher(kafkaTemplate, TOPIC);
		event = new OrderCreatedEvent(UUID.randomUUID(), 15L, "Laptop", 2, Instant.now());
		key = event.getOrderId().toString();
	}

	@Test
	void publishSendsEventToTopicKeyedByOrderIdAndWaitsForConfirmation() {
		when(kafkaTemplate.send(TOPIC, key, event)).thenReturn(CompletableFuture.completedFuture(sendResult()));

		assertThatCode(() -> publisher.publish(event)).doesNotThrowAnyException();

		verify(kafkaTemplate).send(TOPIC, key, event);
	}

	@Test
	void publishThrowsWhenKafkaRejectsTheMessage() {
		KafkaException cause = new KafkaException("broker unavailable");
		when(kafkaTemplate.send(TOPIC, key, event)).thenReturn(CompletableFuture.failedFuture(cause));

		assertThatThrownBy(() -> publisher.publish(event)).isInstanceOf(OrderPublishException.class)
			.hasCause(cause);
	}

	@Test
	void publishThrowsWhenSendFailsImmediately() {
		KafkaException cause = new KafkaException("metadata not available");
		when(kafkaTemplate.send(TOPIC, key, event)).thenThrow(cause);

		assertThatThrownBy(() -> publisher.publish(event)).isInstanceOf(OrderPublishException.class)
			.hasCause(cause);
	}

	private SendResult<String, OrderCreatedEvent> sendResult() {
		return new SendResult<>(new ProducerRecord<>(TOPIC, key, event),
				new RecordMetadata(new TopicPartition(TOPIC, 0), 42, 0, 0, 0, 0));
	}

}
