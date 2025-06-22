package hello.backendproject.stompwebsocket.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisPublisher {
    private final StringRedisTemplate stringRedisTemplate;

    // stomp -> pub -> sub -> stomp
    public void publish(String channel, String mesage) {
        stringRedisTemplate.convertAndSend(channel, mesage);
    }
}
