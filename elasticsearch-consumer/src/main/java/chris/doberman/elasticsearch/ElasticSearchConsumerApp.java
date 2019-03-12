package chris.doberman.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
@Slf4j
public class ElasticSearchConsumerApp {

	public static void main(String[] args) throws IOException {
		ConfigurableApplicationContext ctx = SpringApplication.run(ElasticSearchConsumerApp.class, args);

		TwitterConsumer twitterConsumer = ctx.getBean(TwitterConsumer.class);
		twitterConsumer.run();
	}

}
