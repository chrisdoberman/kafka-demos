package chris.doberman.elasticsearch;

import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;

@Slf4j
@Component
public class TwitterConsumer {

    private final ElasticSearchClient elasticSearchClient;

    private static final String bootstrapServers = "localhost:9092";
    private static final String groupId = "kafka-demo-elasticsearch";
    private static final String topic = "twitter_tweets";
    
    private final KafkaConsumer<String, String> consumer;
    private static JsonParser jsonParser = new JsonParser();

    public TwitterConsumer(ElasticSearchClient elasticSearchClient, KafkaConsumer<String, String> consumer) {
        this.elasticSearchClient = elasticSearchClient;
        this.consumer = consumer;
        this.consumer.subscribe(Arrays.asList(topic));
    }

    public void run() throws IOException {
        log.info("Starting twitter consumer...");

        while (true) {
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100)); // new in Kafka 2.0.0

            Integer recordCount = records.count();
            log.info("Received " + recordCount + " records");

            BulkRequest bulkRequest = new BulkRequest();

            for (ConsumerRecord<String, String> record : records) {

                // 2 strategies
                // kafka generic ID
                // String id = record.topic() + "_" + record.partition() + "_" + record.offset();

                // twitter feed specific id
                try {
                    String id = extractIdFromTweet(record.value());

                    // where we insert data into ElasticSearch
                    IndexRequest indexRequest = new IndexRequest(
                            "twitter",
                            "tweets",
                            id // this is to make our consumer idempotent
                    ).source(record.value(), XContentType.JSON);

                    bulkRequest.add(indexRequest); // we add to our bulk request (takes no time)
                } catch (NullPointerException e) {
                    log.warn("skipping bad data: " + record.value());
                }

            }
            if (recordCount > 0) {
                BulkResponse response = elasticSearchClient.bulk(bulkRequest);
                log.info("bulk response status: {}", response.status());

                log.info("Committing offsets...");
                consumer.commitSync();
                log.info("Offsets have been committed");
                try {
                    Thread.sleep(1000); // for demo purposes
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        // TODO: close elasticsearch client gracefully and the consumer gracefully
    }

    private static String extractIdFromTweet(String tweetJson){
        // gson library
        return jsonParser.parse(tweetJson)
                .getAsJsonObject()
                .get("id_str")
                .getAsString();
    }

}


