package com.example.orderservice.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.orderservice.model.OrderCreatedEvent;
import com.example.orderservice.model.OrderRequest;

@Service
public class OrderService {

	private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

	private final String topic;

	public OrderService(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
			@Value("${app.kafka.topics.order-created}") String topic) {
		this.kafkaTemplate = kafkaTemplate;
		this.topic = topic;
	}

	public OrderCreatedEvent createOrder(OrderRequest request) {
		OrderCreatedEvent event = new OrderCreatedEvent(UUID.randomUUID(), request.getCustomerId(),
				request.getProduct(), request.getQuantity(), Instant.now());
		kafkaTemplate.send(topic,event.getOrderId().toString(), event);
		return event;
	}

}
