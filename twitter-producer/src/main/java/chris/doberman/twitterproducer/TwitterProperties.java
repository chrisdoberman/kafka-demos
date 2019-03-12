package chris.doberman.twitterproducer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@PropertySource("classpath:twitter.properties")
@ConfigurationProperties
@Data
public class TwitterProperties {

    private String consumerKey;
    private String consumerSecret;
    private String token;
    private String secret;
    private List<String> terms;
}
