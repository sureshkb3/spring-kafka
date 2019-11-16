package com.kc.kafkaTwitter.service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

@Service
public class TwitterProducerService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private KafkaProducer<String, String> kafkaProducer;

	private Client client = null;

	public void run() {

		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>();
		client = createTwitterClient(msgQueue);

		client.connect();

		while (!client.isDone()) {
			try {
				String msg = msgQueue.poll(5, TimeUnit.SECONDS);
				logger.info(msg);
				if(msg != null && !msg.trim().equals("")) {
					kafkaProducer.send(new ProducerRecord<String, String>("marvel_dc_tweets", msg),
							(RecordMetadata metadata, Exception exception) -> {
								if (exception != null) {
									logger.error("Error while producing tweet to Kafka.", exception);
								}
							});
				}
				
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
				client.stop();
			}

		}

	}

	public Client createTwitterClient(BlockingQueue<String> msgQueue) {

		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		// Optional: set up some followings and track terms
		List<String> terms = Lists.newArrayList("mcu", "avengers", "dceu", "marvel", "comics", "dc comics", "superhero", "dc extended universe", "warner bros", "warner brothers");
		hosebirdEndpoint.trackTerms(terms);

		Authentication hosebirdAuth = new OAuth1("",
				"",
				"", ""); // your twitter access tokens to get the tweets

		ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01") // optional: mainly for the logs
				.hosts(hosebirdHosts).authentication(hosebirdAuth).endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue)); // optional: use this if you want to process client
																	// events

		Client hosebirdClient = builder.build();

		return hosebirdClient;

	}

	@PreDestroy
	public void destroy() {
		logger.info("Closing Twitter Producer and Client");
		if (client != null) {
			client.stop();
		}

		kafkaProducer.close();
	}

}
