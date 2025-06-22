package hello.backendproject.purewebsocket.room.controller;

import hello.backendproject.purewebsocket.room.entity.ChatRoom;
import hello.backendproject.purewebsocket.room.repository.RoomRepository;
import hello.backendproject.purewebsocket.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    public List<ChatRoom> getAllRooms() {
        return roomService.findAllRooms();
    }

    @PostMapping("/{roomId}")
    public ChatRoom createRoom(@PathVariable String roomId) {
        return roomService.createRoom(roomId);
    }
}
