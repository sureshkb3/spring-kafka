package com.sapient.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TweetController {

	@Autowired
	private TweetProcessor tweetProcessor;

	@GetMapping("/characterCount")
	public String getCharacterCount() {
		return tweetProcessor.getCharacterTweetsCount();
	}

}
