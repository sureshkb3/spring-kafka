package com.sapient.kafka.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

	@Autowired
	private SampleSource source;

	@GetMapping("/sample/{message}")
	public String sample(@PathVariable String message) {
		source.sampleMessageChannel().send(MessageBuilder.withPayload(message).build());
		return "Success";
	}

}
