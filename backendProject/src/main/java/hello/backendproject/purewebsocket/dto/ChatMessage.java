package hello.backendproject.purewebsocket.dto;

import lombok.Getter;

@Getter
public class ChatMessage {

    private String roomId;
    private String message;
    private String from;
}
