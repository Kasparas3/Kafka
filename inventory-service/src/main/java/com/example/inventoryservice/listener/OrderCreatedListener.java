package com.example.inventoryservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.inventoryservice.model.OrderCreatedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderCreatedListener {

	@KafkaListener(topics = "${app.kafka.topics.order-created}", groupId = "inventory-service")
	public void onOrderCreated(OrderCreatedEvent event) {
		log.info("Processing order: OrderId: {}, Product: {}, Quantity: {}", event.getOrderId(), event.getProduct(),
				event.getQuantity());
	}

}
