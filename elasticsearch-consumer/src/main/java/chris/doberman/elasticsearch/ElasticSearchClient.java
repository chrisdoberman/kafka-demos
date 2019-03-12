package chris.doberman.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Slf4j
@Component
public class ElasticSearchClient {

    private final ElasticSearchProperties elasticSearchProperties;
    private final RestHighLevelClient restHighLevelClient;


    public ElasticSearchClient(ElasticSearchProperties elasticSearchProperties) {
        this.elasticSearchProperties = elasticSearchProperties;

        // credentials provider help supply username and password
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(elasticSearchProperties.getUsername(),
                        elasticSearchProperties.getPassword()));

        HttpHost httpHost = new HttpHost(elasticSearchProperties.getHostname(), 443, "https");
        RestClientBuilder builder = RestClient.builder(httpHost)
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

        this.restHighLevelClient = new RestHighLevelClient(builder);
    }

    public BulkResponse bulk(BulkRequest request) throws IOException {
        return restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
    }



}
