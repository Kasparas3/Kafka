package com.example.inventoryservice.model;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class OrderCreatedEvent {

	private UUID orderId;

	private Long customerId;

	private String product;

	private Integer quantity;

	private Instant createdTime;

}
