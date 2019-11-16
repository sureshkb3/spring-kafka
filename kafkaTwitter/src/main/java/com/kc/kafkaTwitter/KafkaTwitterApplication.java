package com.kc.kafkaTwitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.kc.kafkaTwitter.service.TwitterProducerService;

@SpringBootApplication
public class KafkaTwitterApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(KafkaTwitterApplication.class, args);
	}

	
	@Autowired
	private TwitterProducerService twitterProducerService;
	 
	

	@Override
	public void run(String... args) throws Exception {
		twitterProducerService.run();
	}

}
