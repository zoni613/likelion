package hello.backendproject.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.backendproject.auth.dto.LoginRequestDTO;
import hello.backendproject.auth.dto.SignUpRequestDTO;
import hello.backendproject.auth.service.AuthService;
import hello.backendproject.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDTO dto) {
        try {
            authService.signUp(dto);
            return ResponseEntity.ok("회원가입 성공.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()); // 401 반환
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO dto) {
        try {
            UserDTO loginUser = authService.login(dto);

            System.out.println("로그인 성공 = " + new ObjectMapper().writeValueAsString(loginUser));

            return ResponseEntity.ok(loginUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 반환
        }
    }
}
