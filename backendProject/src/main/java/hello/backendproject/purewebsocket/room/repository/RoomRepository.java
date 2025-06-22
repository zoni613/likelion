package hello.backendproject.purewebsocket.room.repository;

import hello.backendproject.purewebsocket.dto.ChatMessage;
import hello.backendproject.purewebsocket.room.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRoomId(String roomId);
}
