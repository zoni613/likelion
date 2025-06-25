package hello.backendproject.user.controller;

import hello.backendproject.security.core.CustomUserDetails;
import hello.backendproject.user.dto.UserDTO;
import hello.backendproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user") //변경
@RequiredArgsConstructor
public class UserController {

    //삭제
//    @Value("${PROJECT_NAME:web Server}")
//    private String instansName;
//
//    @GetMapping
//    public String test(){
//        return instansName;
//    }

    private final UserService userService;

    // 내 정보 보기 /
    @GetMapping("/me")
    // @AuthenticationPrincipal: 스프링 시큐리티에서 인증된 사용자 정보를 자동으로 주입받는 어노테이션
    // 요청 헤더 안에 있는 JWT 토큰에서 사용자 정보를 읽어옴
    public ResponseEntity<UserDTO> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long id = userDetails.getId();
        return ResponseEntity.ok(userService.getMyInfo(id));
    }

    // 유저 정보 수정 /
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateUser(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UserDTO dto)  {
        Long id = userDetails.getId();
        UserDTO updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(updated);
    }

    //아래는 순환참조가 되는  예제
//    @GetMapping("/profile/{profileId}")
//    public User getProfile2(@PathVariable Long profileId)  {
//        return userService.getProfile2(profileId);
//    }



}