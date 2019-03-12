package chris.doberman.twitterproducer;


import com.twitter.hbc.core.Client;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TwitterProducer {

    private final KafkaProducer<String, String> producer;
    private final Client client;
    private final BlockingQueue<String> msgQueue;

    public TwitterProducer(KafkaProducer<String, String> producer, TwitterClient twitterClient) {
        this.producer = producer;
        this.client = twitterClient.getClient();
        this.msgQueue = twitterClient.getMsgQueue();
    }

    public void run() {
        log.info("Starting twitter client.");

        client.connect();

        // add a shutdown hook
        addShutdownHook();

        // loop to send tweets to kafka
        // on a different thread, or multiple different threads....
        while (!client.isDone()) {
            String msg = null;
            try {
                msg = msgQueue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Error during message queue poll", e);
                client.stop();
            }
            if (msg != null){
                log.info(msg);
                producer.send(new ProducerRecord<>("twitter_tweets", null, msg), (recordMetadata, e) -> {
                    if (e != null) {
                        log.error("Something bad happened", e);
                    }
                });
            }
        }
        log.info("End of application");
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("stopping application...");
            log.info("shutting down client from twitter...");
            client.stop();
            log.info("closing producer...");
            producer.close();
            log.info("done!");
        }));
    }
}
