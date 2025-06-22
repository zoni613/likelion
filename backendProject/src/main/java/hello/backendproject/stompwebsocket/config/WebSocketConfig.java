package hello.backendproject.stompwebsocket.config;

import hello.backendproject.purewebsocket.handler.ChatWebSocketHandler;
import hello.backendproject.stompwebsocket.handler.CustomHandshakeHanlder;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /topic 일반 채팅을 받을 접두어
        // /queue 귓속말을 받을 접두어
        /** 서버가 보내는 메시지를 클라이언트가 구독할 때 사용하는 경로 **/
        registry.enableSimpleBroker("/topic", "/queue"); // 구독용 경로 서버 -> 클라이언트

        /** 전송용 Prefix **/

        // 클라이언트가 서버에게 메세지를 보낼 접두어
        /**클라이언트가 서버에 메시지를 보낼 때 사용하는 경로 접두어   ->   @MessageMapping **/
        registry.setApplicationDestinationPrefixes("/app"); //  클라이언트 ->  서버

        // /user 특정 사용자에게 메세지를 보낼 접두어
        /** 서버가 특정 사용자에게 메시지를 보낼 때, 클라이언트가 구독할 경로 접두어 **/
        registry.setUserDestinationPrefix("/user"); // 서버 -> 특정 사용자
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 단체방
        registry.addEndpoint("/ws-chat")
                .setHandshakeHandler(new CustomHandshakeHanlder())
                .setAllowedOriginPatterns("*");

        // gpt endpoint
        registry.addEndpoint("/ws-gpt")
                .setAllowedOriginPatterns("*");
    }
}
