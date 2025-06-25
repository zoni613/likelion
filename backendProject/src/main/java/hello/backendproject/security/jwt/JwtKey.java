package hello.backendproject.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class JwtKey {

    @Value("${jwt.secretKey}")
    private String secret;

    // 서명키를 만들어서 반환하는 메서드
    @Bean
    public SecretKey secretKey() {
        byte[] keyBytes = secret.getBytes(); // 설정파일에서 불러온 키 값을 바이트 배열로 변환
        return new SecretKeySpec(keyBytes, "HmacSHA512"); // 바이트 배열을 HmacSHA512용 Security 객체로 매핑
    }
}
