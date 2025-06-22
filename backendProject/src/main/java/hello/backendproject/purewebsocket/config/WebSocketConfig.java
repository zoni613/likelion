package hello.backendproject.purewebsocket.config;

import hello.backendproject.purewebsocket.handler.ChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

//@Configuration
//@EnableWebSocket // websocket을 사용하기 위한 어노테이션
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(), "/ws-chat")
                .setAllowedOriginPatterns("*");
                // ws-chat 엔드포인트로 요청을 보낼 수 있는지 결정하는 보안 정책(CORS) 설정
                // * -> 모든 도메인에서 접근 가능
    }
}
