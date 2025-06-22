package hello.backendproject.stompwebsocket.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.backendproject.stompwebsocket.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private ObjectMapper objectMapper =  new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String msgBody = new String(message.getBody());
            ChatMessage chatMessage = objectMapper.readValue(msgBody, ChatMessage.class);
            if(chatMessage.getTo() != null && !chatMessage.getTo().isEmpty()) {
                simpMessagingTemplate.convertAndSendToUser(chatMessage.getTo(), "/queue/private", chatMessage);
            } else {
                simpMessagingTemplate.convertAndSend("/topic/room." + chatMessage.getRoomId(), chatMessage);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
