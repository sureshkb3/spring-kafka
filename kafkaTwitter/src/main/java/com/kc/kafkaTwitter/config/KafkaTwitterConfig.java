package com.kc.kafkaTwitter.config;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTwitterConfig {

	@Bean
	public KafkaProducer<String, String> kafkaProducer() {
		Properties props = new Properties();

		props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		// Safe Producer
		props.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
		props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
		props.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE + "");
		props.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

		// message compression
		props.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
		props.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
		props.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.valueOf(32 * 1024).toString());

		return new KafkaProducer<>(props);
	}

}
