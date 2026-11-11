package com.example.orderservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.orderservice.kafka.OrderEventPublisher;
import com.example.orderservice.model.OrderCreatedEvent;
import com.example.orderservice.model.OrderRequest;

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

		OrderCreatedEvent result = orderService.createOrder(request);

		ArgumentCaptor<OrderCreatedEvent> captor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
		verify(orderEventPublisher).publish(captor.capture());
		OrderCreatedEvent published = captor.getValue();

		assertThat(published).isSameAs(result);
		assertThat(published.getOrderId()).isNotNull();
		assertThat(published.getCustomerId()).isEqualTo(15L);
		assertThat(published.getProduct()).isEqualTo("Laptop");
		assertThat(published.getQuantity()).isEqualTo(2);
		assertThat(published.getCreatedTime()).isBetween(before, Instant.now());
	}

	@Test
	void createOrderGeneratesUniqueOrderIds() {
		OrderRequest request = new OrderRequest();

		OrderCreatedEvent first = orderService.createOrder(request);
		OrderCreatedEvent second = orderService.createOrder(request);

		assertThat(first.getOrderId()).isNotEqualTo(second.getOrderId());
	}

}
