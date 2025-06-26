package hello.backendproject.security.jwt;

import hello.backendproject.security.core.CustomUserDetailService;
import hello.backendproject.security.core.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// JwtTokenFilter 모든 HTTP 요청을 가로채서 JWT 토큰을 검사하는 필터 역할
// OncePerRequestFilter는 한 요청 당 딱 한 번만 실행되는 필터 역할
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailService customUserService;

    // HTTP 매 요청마다 호출
    @Override
    protected void doFilterInternal(HttpServletRequest request, // http 요청
                                    HttpServletResponse response, // http 응답
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        String accessToken = getTokenFromRequest(request); // 요청 헤더에서 토큰 추출

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) { // 토큰이 있고, 유효할 때

            // 토큰에서 사용자를 꺼내서 담은 사용자 인증 객체
            UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(accessToken);

            // http요청으로브터 부가 정보(ip, 세션 등)를 추출해 사용자 인증 객체에 넣어줌
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


            // 토큰에서 사용자 인증정보를 조회해 현재 스레드에 인증된 사용자로 등록
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            String url = request.getRequestURI().toString();
            String method = request.getMethod(); // GET, POST, PUT
            System.out.println("[" + method + "] 현재 들어온 HTTP 요청: " + url);
        }

        /**
         * CharactorEncodingFilter: 문자 인코딩 처리
         * CorsFilter: 정책 처리
         * CsrfFilter: 보안 처리
         * JWTTokenFilter: JWT 토큰 처리(핵심)
         * SecurityContextFilter: 인증/인가 정보 저장
         * ExceptionFilter: 예외처리
         * */

        filterChain.doFilter(request, response); // JwtTokenFilter를 거쳐 다음 필터로 넘김
    }

    // HTTP 요청 헤더에서 토큰을 추출하는 메서드
    public String getTokenFromRequest(HttpServletRequest request) {
        String token = null;

        String bearerToken = request.getHeader("Authorization");
        // hasText(): 문자열이 null이 아니고 공백이 아닌 실제 텍스트가 존재하는지 확인
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }

        return token;
    }

    // http 요청에서 사용자 인증 정보를 담는 객체
    private UsernamePasswordAuthenticationToken getAuthentication(String token) {

        // JWT 토큰에서 사용자 id 추출
        Long userid = jwtTokenProvider.getUserIdFromToken(token);

        // 추출한 id로 DB에서 사용자 정보 조회
        UserDetails userDetails = customUserService.loadUserById(userid);

        return new UsernamePasswordAuthenticationToken(
                userDetails, // 사용자 정보
                null, // credential 영역인데 이미 인증이 된 상태이기 때문에 생략
                userDetails.getAuthorities() // 사용자 권한
        );

    }
}
