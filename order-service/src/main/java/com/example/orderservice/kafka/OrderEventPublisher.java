package com.example.orderservice.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.orderservice.model.OrderCreatedEvent;

@Component
public class OrderEventPublisher {

	private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

	private final String topic;

	public OrderEventPublisher(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
			@Value("${app.kafka.topics.order-created}") String topic) {
		this.kafkaTemplate = kafkaTemplate;
		this.topic = topic;
	}

	public void publish(OrderCreatedEvent event) {
		kafkaTemplate.send(topic, event.getOrderId().toString(), event);
	}

}
