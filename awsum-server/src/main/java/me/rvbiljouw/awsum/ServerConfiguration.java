package me.rvbiljouw.awsum;

import com.rabbitmq.client.ConnectionFactory;
import me.rvbiljouw.awsum.spotify.SpotifyClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;

/**
 * @author rvbiljouw
 */
@Configuration
@EnableScheduling
@PropertySource(value = "classpath:server.properties", ignoreResourceNotFound = true)
public class ServerConfiguration {
    @Value("${spotify.clientId:undefined}")
    private String spotifyClientId;
    @Value("${spotify.clientSecret:undefined}")
    private String spotifyClientSecret;
    @Value("${spotify.redirectURI:undefined}")
    private String spotifyRedirectUri;
    @Value("${rabbitmq.uri:undefined}")
    private String rabbitmqUri;

    @Bean
    @Scope("singleton")
    ConnectionFactory getAmqpConnectionFactory() throws Exception {
        final ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(rabbitmqUri);
        return factory;
    }


    @Bean(name = "serverSpotifyClient")
    @Scope(value = "singleton")
    SpotifyClient getServerSpotifyClient() throws URISyntaxException {
        return new SpotifyClient.Builder()
                .setClientId(spotifyClientId)
                .setClientSecret(spotifyClientSecret)
                .setRedirectURI(new URI(spotifyRedirectUri))
                .build();
    }

    @Bean(name="sessionTaskExecutor")
    TaskExecutor getSessionTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newSingleThreadExecutor());
    }


}
