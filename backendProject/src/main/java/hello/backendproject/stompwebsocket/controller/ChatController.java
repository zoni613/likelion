package hello.backendproject.stompwebsocket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.backendproject.stompwebsocket.dto.ChatMessage;
import hello.backendproject.stompwebsocket.gpt.GPTService;
import hello.backendproject.stompwebsocket.redis.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    // 서버가 클라이언트에게 수동으로 메세지를 보낼 수 있도록 하는 클래스
    private final SimpMessagingTemplate template;

    @Value("${PROJECT_NAME:web Server}")
    private String instnasName;

    private final RedisPublisher redisPublisher;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final GPTService gptService;

    // 단일 브로드캐스트
    @MessageMapping("/gpt")
    public void sendMessageGPT(ChatMessage message) throws Exception {
        template.convertAndSend("/topic/gpt", message); // 내가 보낸 메세지 출력
        
        // gpt  메세지 반환
        String getResponse = gptService.gptMessage(message.getMessage());

        ChatMessage chatMessage = new ChatMessage("GPT", getResponse);

        template.convertAndSend("/topic/gpt", chatMessage);
    }

    @MessageMapping("/chat.sendMessage")
    public void sendmessage(ChatMessage message) throws JsonProcessingException {
        /*
        // instanceName 확인 필요 시
        message.setMessage(instnasName + message.getMessage());
        if(message.getTo() != null && !message.getTo().isEmpty()) {
            // 귓속말
            // 내 아이디로 귓속말 경로를 활성화 함
            template.convertAndSendToUser(message.getTo(), "/queue/private", message);
        } else {
            // 일반 메시지
            // message에서 roomId를 추출해서 해당 roomId를 구독하고 있는 클라이언트에게 메세지를 전달
            template.convertAndSend("/topic/" + message.getRoomId(), message);
        }
         */
        String channel = "";
        String msg = "";

        if(message.getTo() != null && !message.getTo().isEmpty()) {
            channel = "private." + message.getRoomId();
            msg = objectMapper.writeValueAsString(message);
        } else {
            channel = "room." + message.getRoomId();
            msg = objectMapper.writeValueAsString(message);
        }

        redisPublisher.publish(channel, msg);
    }
}

