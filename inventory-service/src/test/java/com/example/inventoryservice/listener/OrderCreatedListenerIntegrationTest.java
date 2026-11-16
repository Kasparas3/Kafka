package com.example.inventoryservice.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.Map;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

@SpringBootTest(properties = { "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
		"spring.kafka.consumer.auto-offset-reset=earliest" })
@EmbeddedKafka(partitions = 1, topics = "order-created")
class OrderCreatedListenerIntegrationTest {

	private static final String TOPIC = "order-created";

	@Autowired
	private EmbeddedKafkaBroker broker;

	@Test
	void malformedMessageIsSkippedAndNextOrderIsProcessed() throws Exception {
		Map<String, Object> props = KafkaTestUtils.producerProps(broker);
		try (KafkaProducer<String, String> producer = new KafkaProducer<>(props, new StringSerializer(),
				new StringSerializer())) {
			producer.send(new ProducerRecord<>(TOPIC, "bad", "this is not json")).get();
			producer.send(new ProducerRecord<>(TOPIC, "922a0957-91da-44d6-845f-b30e70b2a1bf", """
					{"orderId":"922a0957-91da-44d6-845f-b30e70b2a1bf","customerId":15,"product":"Laptop",\
					"quantity":2,"createdTime":"2026-11-06T07:24:24Z"}""")).get();
		}

		await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
			OffsetAndMetadata committed = KafkaTestUtils.getCurrentOffset(broker.getBrokersAsString(),
					"inventory-service", TOPIC, 0);
			assertThat(committed).isNotNull();
			assertThat(committed.offset()).isEqualTo(2);
		});
	}

}
