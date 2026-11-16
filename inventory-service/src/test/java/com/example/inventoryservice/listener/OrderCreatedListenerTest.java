package com.example.inventoryservice.listener;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import com.example.inventoryservice.model.OrderCreatedEvent;

@ExtendWith(OutputCaptureExtension.class)
class OrderCreatedListenerTest {

	private final OrderCreatedListener listener = new OrderCreatedListener();

	@Test
	void onOrderCreatedLogsOrderDetails(CapturedOutput output) {
		OrderCreatedEvent event = new OrderCreatedEvent();
		event.setOrderId(UUID.fromString("922a0957-91da-44d6-845f-b30e70b2a1bf"));
		event.setCustomerId(15L);
		event.setProduct("Laptop");
		event.setQuantity(2);
		event.setCreatedTime(Instant.now());

		listener.onOrderCreated(event);

		assertThat(output).contains(
				"Processing order: OrderId: 922a0957-91da-44d6-845f-b30e70b2a1bf, Product: Laptop, Quantity: 2");
	}

}
