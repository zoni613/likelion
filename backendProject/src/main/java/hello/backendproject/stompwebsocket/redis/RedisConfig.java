package hello.backendproject.stompwebsocket.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

//@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisSubscriber redisSubscriber;

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer Container = new RedisMessageListenerContainer();
        Container.setConnectionFactory(redisConnectionFactory);

        Container.addMessageListener(new MessageListenerAdapter(redisSubscriber),new PatternTopic("room.*"));
        Container.addMessageListener(new MessageListenerAdapter(redisSubscriber),new PatternTopic("private.*"));

        return Container;

    }
}
