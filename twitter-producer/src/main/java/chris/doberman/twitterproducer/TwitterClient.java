package chris.doberman.twitterproducer;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class TwitterClient {

    private final TwitterProperties twitterProperties;
    private final Client client;
    private final BlockingQueue<String> msgQueue;

    public TwitterClient(TwitterProperties twitterProperties) {
        this.twitterProperties = twitterProperties;
        this.msgQueue = new LinkedBlockingQueue<String>(1000);

        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();

        hosebirdEndpoint.trackTerms(twitterProperties.getTerms());

        Authentication hosebirdAuth = new OAuth1(twitterProperties.getConsumerKey(),
                twitterProperties.getConsumerSecret(), twitterProperties.getToken(), twitterProperties.getSecret());

        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")                              // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        this.client = builder.build();
    }

    public Client getClient() {
        return client;
    }

    public BlockingQueue<String> getMsgQueue() {
        return msgQueue;
    }
}
