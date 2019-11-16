package com.sapient.kafka.publisher;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface SampleSource {
	
	@Output("sampleMessageChannel")
	MessageChannel sampleMessageChannel();

}
