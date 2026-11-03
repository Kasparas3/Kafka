package com.example.orderservice.model;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderCreatedEvent {

	private UUID orderId;

	private Long customerId;

	private String product;

	private Integer quantity;

	private Instant createdTime;

}
