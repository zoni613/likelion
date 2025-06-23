package hello.backendproject.purewebsocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.backendproject.purewebsocket.dto.ChatMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    // Session(id) 관리 객체 -> set: 중복, synchronizedSet -> 동시성 방지
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    // json <-> JAVA 객체 변환
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 방과 방 안에 있는 세션을 관리하는 객체
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap();

    // 클라이언트가 웹소켓서버에 접속했을 때 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 접속 시 sessions에 등록
        sessions.add(session);

        System.out.println("접속된 클라이언트 세션 ID =  " + session.getId());
    }

    // 클러이언트가 보낸 메세지를 서버가 받았을 때 호출
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // json 문자열 -> JAVA 객체
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);

        String roodID = chatMessage.getRoomId(); // 클라이언트에게 받은 메세지에서 roomID를 추출

        if(!rooms.containsKey(roodID)){
            // 해당 방이 없을 때 새로운 방 생성
            rooms.put(roodID, ConcurrentHashMap.newKeySet());
        }

        // 해당 방에 session을 추가
        rooms.get(roodID).add(session);

        for(WebSocketSession s : rooms.get(roodID)){
            if(s.isOpen()) {
                // json 문자로 변경해서 메세지를 보내준다.
                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                System.out.println("전송된 메세지 = " + chatMessage.getMessage());
            }
        }
        /* 방이 없을 경우
        for (WebSocketSession s : sessions) {
            if(s.isOpen()) {
                // json 문자로 변경해서 메세지를 보내준다.
                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                System.out.println("전송된 메세지 = " + chatMessage.getMessage());
            }
        }
        */
    }

    // 클러이언트가 연결이 끊어졌을 때 호출
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);

        // 연결이 해제되면 소속되어 있는 방에서 제거
        for(Set<WebSocketSession> room  : rooms.values()){
            room.remove(session);
        }
    }
}
