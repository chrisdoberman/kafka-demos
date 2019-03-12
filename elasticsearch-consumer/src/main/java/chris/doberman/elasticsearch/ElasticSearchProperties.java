package chris.doberman.elasticsearch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:elasticsearch.properties")
@ConfigurationProperties
@Data
public class ElasticSearchProperties {

    private String hostname;
    private String username;
    private String password;
}
