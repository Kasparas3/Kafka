package com.example.orderservice.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.orderservice.model.OrderCreatedEvent;
import com.example.orderservice.model.OrderRequest;

@Service
public class OrderService {

	private static final String TOPIC = "order-created";

	private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

	public OrderService(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public OrderCreatedEvent createOrder(OrderRequest request) {
		OrderCreatedEvent event = new OrderCreatedEvent(UUID.randomUUID(), request.getCustomerId(),
				request.getProduct(), request.getQuantity(), Instant.now());
		kafkaTemplate.send(TOPIC, event.getOrderId().toString(), event);
		return event;
	}

}
