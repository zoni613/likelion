package hello.backendproject.stompwebsocket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private String message;
    private String from;

    private String to; // 귓속말을 받을 사람
    private String roomId; // 방 id

    public ChatMessage(String message, String from) {
        this.message = message;
        this.from = from;
    }
}
