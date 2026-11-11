package com.example.orderservice.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.orderservice.kafka.OrderEventPublisher;
import com.example.orderservice.model.OrderCreatedEvent;
import com.example.orderservice.model.OrderRequest;
import com.example.orderservice.model.OrderResponse;

@Service
public class OrderService {

	private final OrderEventPublisher orderEventPublisher;

	public OrderService(OrderEventPublisher orderEventPublisher) {
		this.orderEventPublisher = orderEventPublisher;
	}

	public OrderResponse createOrder(OrderRequest request) {
		OrderCreatedEvent event = new OrderCreatedEvent(UUID.randomUUID(), request.getCustomerId(),
				request.getProduct(), request.getQuantity(), Instant.now());
		orderEventPublisher.publish(event);
		return new OrderResponse(event.getOrderId(), event.getCustomerId(), event.getProduct(), event.getQuantity(),
				event.getCreatedTime());
	}

}
