package hello.backendproject.purewebsocket.room.service;

import hello.backendproject.purewebsocket.room.entity.ChatRoom;
import hello.backendproject.purewebsocket.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public ChatRoom createRoom(String roomId) {
        return roomRepository.findByRoomId(roomId)
                .orElseGet(() -> {
                    ChatRoom chatRoom = new ChatRoom();
                    chatRoom.setRoomId(roomId);
                    return roomRepository.save(chatRoom);
                });
    }

    public List<ChatRoom> findAllRooms() {
        return roomRepository.findAll();
    }
}
