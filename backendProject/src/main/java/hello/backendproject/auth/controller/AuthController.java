package hello.backendproject.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.backendproject.auth.dto.LoginRequestDTO;
import hello.backendproject.auth.dto.LoginResponseDTO;
import hello.backendproject.auth.dto.SignUpRequestDTO;
import hello.backendproject.auth.service.AuthService;
import hello.backendproject.user.dto.UserDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        LoginResponseDTO loginUser = authService.login(dto);
        return ResponseEntity.ok(loginUser);
    }

    /**
     * 토큰 갱신 API
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader(value = "Authorization", required = false)
                                          String authorizationHeader, HttpServletRequest request) {
        String refreshToken = null;

        // 1. 쿠키에서 찾기
        if(request.getCookies() != null) {
            for(Cookie cookie : request.getCookies()) {
                if("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        // 2. Authorization 헤더 찾기
        if(refreshToken == null && authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            refreshToken = authorizationHeader.replace("Bearer ", "").trim();
        }

        if(refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("리프래시 토큰이 없습니다.");
        }

        String newAcessToken = authService.refreshToken(refreshToken);
        //json 객체로 변환하여 front에 내려주기
        Map<String, String> res = new HashMap<>();
        res.put("accessToken", newAcessToken);
        res.put("refreshToken", refreshToken);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        // accessToken 쿠키 삭제
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0); // 즉시 만료!

        // refreshToken 쿠키 삭제
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);

        // 응답에 쿠키 삭제 포함
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        // (추가) 서버 세션도 있다면 만료
        // request.getSession().invalidate();

        return ResponseEntity.ok().body("로그아웃 완료 (쿠키 삭제됨)");
    }
}
