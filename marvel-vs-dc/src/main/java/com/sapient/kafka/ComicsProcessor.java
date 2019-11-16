package com.sapient.kafka;

import java.util.Arrays;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
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
public class ComicsProcessor {

	public static final String INPUT_TOPIC = "input";
	public static final String OUTPUT_TOPIC = "output";

	String[] mcuCharacters = { "iron man", "spiderman", "captain marvel", "thor", "hulk", "deadpool" };

	private static final String STORE_NAME = "mcu_dc_result_store";

	@Autowired
	private InteractiveQueryService interactiveQueryService;

	@StreamListener(INPUT_TOPIC)
	@SendTo(OUTPUT_TOPIC)
	public KStream<String, Long> processCharacters(KStream<String, Long> input) {
		input.foreach((name, count) -> System.out.println(name + "------------>" + count));
		KTable<String, Long> resultTable = input.map((key, value) -> {
			String newKey = Arrays.stream(mcuCharacters).anyMatch(key::equals) ? "Marvel" : "DC";
			return new KeyValue<String, Long>(newKey, value);
		}).groupByKey().count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as(STORE_NAME)
				.withKeySerde(Serdes.String()).withValueSerde(Serdes.Long()));

		return resultTable.toStream();
	}

	public String getResult() {
		final ReadOnlyKeyValueStore<String, Long> marvelDcStore = interactiveQueryService.getQueryableStore(STORE_NAME,
				QueryableStoreTypes.<String, Long>keyValueStore());

		return "Marvel: " + marvelDcStore.get("Marvel") + ", DC: " + marvelDcStore.get("DC");
	}

}
