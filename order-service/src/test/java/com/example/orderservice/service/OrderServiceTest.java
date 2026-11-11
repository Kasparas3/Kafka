package com.example.orderservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.orderservice.exception.OrderPublishException;
import com.example.orderservice.kafka.OrderEventPublisher;
import com.example.orderservice.model.OrderCreatedEvent;
import com.example.orderservice.model.OrderRequest;
import com.example.orderservice.model.OrderResponse;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	private OrderEventPublisher orderEventPublisher;

	@InjectMocks
	private OrderService orderService;

	@Test
	void createOrderBuildsEventAndPublishesIt() {
		OrderRequest request = new OrderRequest();
		request.setCustomerId(15L);
		request.setProduct("Laptop");
		request.setQuantity(2);
		Instant before = Instant.now();

		orderService.createOrder(request);

		ArgumentCaptor<OrderCreatedEvent> captor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
		verify(orderEventPublisher).publish(captor.capture());
		OrderCreatedEvent published = captor.getValue();

		assertThat(published.getOrderId()).isNotNull();
		assertThat(published.getCustomerId()).isEqualTo(15L);
		assertThat(published.getProduct()).isEqualTo("Laptop");
		assertThat(published.getQuantity()).isEqualTo(2);
		assertThat(published.getCreatedTime()).isBetween(before, Instant.now());
	}

	@Test
	void createOrderReturnsResponseMatchingPublishedEvent() {
		OrderRequest request = new OrderRequest();
		request.setCustomerId(15L);
		request.setProduct("Laptop");
		request.setQuantity(2);

		OrderResponse response = orderService.createOrder(request);

		ArgumentCaptor<OrderCreatedEvent> captor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
		verify(orderEventPublisher).publish(captor.capture());
		OrderCreatedEvent published = captor.getValue();

		assertThat(response.getOrderId()).isEqualTo(published.getOrderId());
		assertThat(response.getCustomerId()).isEqualTo(published.getCustomerId());
		assertThat(response.getProduct()).isEqualTo(published.getProduct());
		assertThat(response.getQuantity()).isEqualTo(published.getQuantity());
		assertThat(response.getCreatedTime()).isEqualTo(published.getCreatedTime());
	}

	@Test
	void createOrderPropagatesPublishFailure() {
		OrderPublishException failure = new OrderPublishException("Failed to publish order", new RuntimeException());
		doThrow(failure).when(orderEventPublisher).publish(any(OrderCreatedEvent.class));

		assertThatThrownBy(() -> orderService.createOrder(new OrderRequest())).isSameAs(failure);
	}

	@Test
	void createOrderGeneratesUniqueOrderIds() {
		OrderRequest request = new OrderRequest();

		OrderResponse first = orderService.createOrder(request);
		OrderResponse second = orderService.createOrder(request);

		assertThat(first.getOrderId()).isNotEqualTo(second.getOrderId());
	}

}
