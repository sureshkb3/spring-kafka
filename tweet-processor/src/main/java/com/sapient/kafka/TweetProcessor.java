package com.sapient.kafka;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.cloud.stream.binder.kafka.streams.annotations.KafkaStreamsProcessor;
import org.springframework.messaging.handler.annotation.SendTo;

@EnableBinding(KafkaStreamsProcessor.class)
public class TweetProcessor {

	public static final String INPUT_TOPIC = "input";
	public static final String OUTPUT_TOPIC = "output";

	String[] mcuCharacters = { "iron man", "spiderman", "captain marvel", "thor", "hulk", "deadpool" };
	String[] dcCharacters = { "batman", "superman", "wonder woman", "joker", "aquaman", "catwoman", "green arrow",
			"harley quinn", "shazam", "green lantern", "flash" };

	private static final String STORE_NAME = "mcu_dc_tweet_store";
	private static final String CHARACTER_STORE_NAME = "mcu_dc_character_count_store";

	@Autowired
	private InteractiveQueryService interactiveQueryService;

	@StreamListener(INPUT_TOPIC)
	@SendTo(OUTPUT_TOPIC)
	public KStream<String, Long> process(KStream<String, String> input) {

		KGroupedStream<String, Long> groupedKStream = input
				// transform values to lower-case and remove dashes for characters matching
				.mapValues(value -> value.toLowerCase().replaceAll("-", ""))
				// filter the tweets containing mcu or dc heros
				.filter((key, value) -> {
					return Arrays.stream(mcuCharacters).parallel().anyMatch(value::contains)
							|| Arrays.stream(dcCharacters).parallel().anyMatch(value::contains);
				})
				// Convert the filtered list into a Kstream with key as the hero name and value 1
				.flatMap((key, value) -> {
					List<KeyValue<String, Long>> result = new LinkedList<>();
					Arrays.stream(mcuCharacters).forEach(mcuCharacter -> {
						if (value.contains(mcuCharacter)) {
							result.add(new KeyValue<String, Long>(mcuCharacter, 1L));
						}
					});

					Arrays.stream(dcCharacters).forEach(dcCharacter -> {
						if (value.contains(dcCharacter)) {
							result.add(new KeyValue<String, Long>(dcCharacter, 1L));
						}
					});

					return result;
				})
				// group by the key (character name) so we can perform aggregation operations
				.groupByKey(Serialized.with(Serdes.String(), Serdes.Long()));

		// perform reduce or aggregate operation and save it in local store for querying purposes
		groupedKStream.reduce((oldCount, newCount) -> oldCount + newCount,
				Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as(CHARACTER_STORE_NAME)
						.withKeySerde(Serdes.String()).withValueSerde(Serdes.Long()));

		KStream<String, Long> resultStream = groupedKStream
				.count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as(STORE_NAME)
						.withKeySerde(Serdes.String()).withValueSerde(Serdes.Long()))
				.toStream();

		return resultStream;
	}

	public String getCharacterTweetsCount() {
		// Query from the local store for request serving purposes
		final ReadOnlyKeyValueStore<String, Long> characterStore = interactiveQueryService
				.getQueryableStore(CHARACTER_STORE_NAME, QueryableStoreTypes.<String, Long>keyValueStore());
		
		
		final StringBuilder marvelResult = new StringBuilder("<b>Marvel Characters <b/><br/>--------------------<br/>");
		final StringBuilder dcResult = new StringBuilder("DC Characters <br/>--------------------<br/>");
		
		characterStore.all().forEachRemaining((characters) -> {
			String value = "<b>" + characters.key + " : " + characters.value + "<b/><br/>";
			if (Arrays.stream(mcuCharacters).anyMatch(characters.key::equals)) {
				marvelResult.append(value);
			} else {
				dcResult.append(value);
			}
		});

		return marvelResult.toString() + "<br/>" + dcResult.toString();
	}

}
