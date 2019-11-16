package com.sapient.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerDemo {

	private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class);

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Properties producerProperties = new Properties();
		producerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		producerProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		producerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(producerProperties);
		for (int i = 1; i <= 20; i++) {
			String key = "key_" + i;
			ProducerRecord<String, String> record = new ProducerRecord<>("sample_topic", key,
					"Message from java kafka producer " + i);
			kafkaProducer.send(record, (RecordMetadata metadata, Exception exception) -> {
				log.info("Topic: {}", metadata.topic());
				log.info("Partition: {}", metadata.partition());
				log.info("Offset: {}", metadata.offset());
				log.info("Timestamp: {}", metadata.timestamp());
			});//.get();
		}

		kafkaProducer.flush();
		kafkaProducer.close();
	}

}
