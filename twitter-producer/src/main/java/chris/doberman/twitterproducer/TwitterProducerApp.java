package chris.doberman.twitterproducer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TwitterProducerApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(TwitterProducerApp.class, args);

        TwitterProducer twitterProducer = ctx.getBean(TwitterProducer.class);
        twitterProducer.run();
    }

}
