package com.example.inventoryservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.inventoryservice.model.OrderCreatedEvent;

@Component
public class OrderCreatedListener {

	@KafkaListener(topics = "order-created", groupId = "inventory-service")
	public void onOrderCreated(OrderCreatedEvent event) {
		System.out.println("Processing order:");
		System.out.println("OrderId: " + event.getOrderId());
		System.out.println("Product: " + event.getProduct());
		System.out.println("Quantity: " + event.getQuantity());
	}

}
