package com.sapient.kafka.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBinding(SampleSource.class)
public class PublisherApplication {
	
	@Autowired
	private ApplicationContextProvider applicationContextProvider;

	public static void main(String[] args) {
		SpringApplication.run(PublisherApplication.class, args);
		Object bean = ApplicationContextProvider.getApplicationContext().getBean("");
		System.out.println(bean);
	}
	


}
