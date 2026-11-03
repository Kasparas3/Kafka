package com.example.orderservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.orderservice.model.OrderCreatedEvent;
import com.example.orderservice.model.OrderRequest;
import com.example.orderservice.service.OrderService;

@RestController
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping("/orders")
	@ResponseStatus(HttpStatus.CREATED)
	public OrderCreatedEvent createOrder(@RequestBody OrderRequest request) {
		return orderService.createOrder(request);
	}

}
