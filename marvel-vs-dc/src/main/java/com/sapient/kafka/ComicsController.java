package com.sapient.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ComicsController {

	@Autowired
	private ComicsProcessor comicsProcessor;

	@GetMapping("/result")
	public String getResult() {
		return comicsProcessor.getResult();
	}

}
