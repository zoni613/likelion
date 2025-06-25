package hello.backendproject.auth.service;

import hello.backendproject.auth.dto.LoginRequestDTO;
import hello.backendproject.auth.dto.LoginResponseDTO;
import hello.backendproject.auth.dto.SignUpRequestDTO;
import hello.backendproject.auth.entity.Auth;
import hello.backendproject.auth.repository.AuthRepository;
import hello.backendproject.security.core.CustomUserDetails;
import hello.backendproject.security.core.Role;
import hello.backendproject.security.jwt.JwtTokenProvider;
import hello.backendproject.user.dto.UserDTO;
import hello.backendproject.user.dto.UserProfileDTO;
import hello.backendproject.user.entity.User;
import hello.backendproject.user.entity.UserProfile;
import hello.backendproject.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.accessTokenExpirationTime}")
    private Long jwtAccessTokenExpirationTime;
    @Value("${jwt.refreshTokenExpirationTime}")
    private Long jwtRefreshTokenExpirationTime;

    // 회원가입
    @Transactional
    public void signUp(SignUpRequestDTO dto) {
        if (userRepository.findByUserid(dto.getUserid()).isPresent()) {
            throw new RuntimeException("사용자가 이미 존재합니다.");
        }

        User user = new User();
        user.setUserid(dto.getUserid());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // 비밀번호 암호화해서 저장
        user.setRole(Role.ROLE_USER); // 일반 사용자로 회원가입

        UserProfile profile = new UserProfile();
        profile.setUsername(dto.getUsername());
        profile.setEmail(dto.getEmail());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());

        // 연관관계 생성
        profile.setUser(user);
        user.setUserProfile(profile);

        userRepository.save(user);
    }

    // 로그인
    public LoginResponseDTO login(LoginRequestDTO dto) {
        // 아이디 검사
        User user = userRepository.findByUserid(dto.getUserid()).orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        // 입력한 비밀번호가 암호화된 비밀번호와 일치하는지 확인
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            // 시큐리티 로그인 과정에서 비밀번호가 일치하지 않으면 던져주는 예외
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 위 비밀번호가 일치하면 기존 토큰 정보를 비교하고 토큰이 있으면 업데이트, 없으면 서로 발급
        // 엑세스 토큰
        String accessToken = jwtTokenProvider.generateToken(new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), user.getPassword()), jwtAccessTokenExpirationTime);

        // 리프레시 토큰
        String refreshToken = jwtTokenProvider.generateToken(new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), user.getPassword()), jwtRefreshTokenExpirationTime);

        // 현재 로그인한 사람이 DB에 있는지 확인하고 있으면 토큰을 DB 저장하고 로그인 완료
        if (authRepository.existsByUser(user)) {
            Auth auth = user.getAuth();
            auth.setRefreshToken(refreshToken);
            auth.setAccessToken(accessToken);
            authRepository.save(auth);

            return new LoginResponseDTO(auth);
        }

        // 위에서 DB에 사용자 정보가 없으면 아래 새로 생성하여 로그인 처리
        Auth auth = new Auth(user, refreshToken, accessToken, "Bearer");
        authRepository.save(auth);

        return new LoginResponseDTO(auth);
    }

    // 리프래시 토큰을 받아서 새로운 엑세스 토큰을 발급해주는 서비스
    @Transactional
    public String refreshToken(String refreshToken) {
        // 리프레시 토큰 유효성 검사
        if (jwtTokenProvider.validateToken(refreshToken)) {
            //DB에서 리프레시 토큰을 가진 사용자가 있는지 확인
            Auth auth = authRepository.findByRefreshToken(refreshToken).orElseThrow(
                    () -> new IllegalArgumentException("해당 REFRESH_TOKEN을 찾을 수 없습니다.\nREFESH_TOKEN = ." + refreshToken));

            String newAccessToken = jwtTokenProvider.generateToken(
                    new UsernamePasswordAuthenticationToken(
                            new CustomUserDetails(auth.getUser()), auth.getUser().getPassword()), jwtAccessTokenExpirationTime); // 엑세스 토큰 만료시간으로 설정

            auth.updateAccessToken(newAccessToken); // 토큰 업데이트
            authRepository.save(auth); // DB에 반영

            return newAccessToken;
        } else {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }
    }

}
