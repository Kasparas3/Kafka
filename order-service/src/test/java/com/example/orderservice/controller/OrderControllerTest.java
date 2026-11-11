package com.example.orderservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.orderservice.exception.OrderPublishException;
import com.example.orderservice.model.OrderRequest;
import com.example.orderservice.model.OrderResponse;
import com.example.orderservice.service.OrderService;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OrderService orderService;

	@Test
	void createOrderReturns201WithOrderResponse() throws Exception {
		UUID orderId = UUID.fromString("922a0957-91da-44d6-845f-b30e70b2a1bf");
		Instant createdTime = Instant.parse("2026-11-06T07:24:24Z");
		when(orderService.createOrder(any(OrderRequest.class)))
			.thenReturn(new OrderResponse(orderId, 15L, "Laptop", 2, createdTime));

		mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"customerId": 15, "product": "Laptop", "quantity": 2}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.orderId").value(orderId.toString()))
			.andExpect(jsonPath("$.customerId").value(15))
			.andExpect(jsonPath("$.product").value("Laptop"))
			.andExpect(jsonPath("$.quantity").value(2))
			.andExpect(jsonPath("$.createdTime").value("2026-11-06T07:24:24Z"));
	}

	@Test
	void createOrderReturns503WhenPublishingFails() throws Exception {
		when(orderService.createOrder(any(OrderRequest.class)))
			.thenThrow(new OrderPublishException("Failed to publish order", new RuntimeException("broker down")));

		mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"customerId": 15, "product": "Laptop", "quantity": 2}
					"""))
			.andExpect(status().isServiceUnavailable());
	}

}
