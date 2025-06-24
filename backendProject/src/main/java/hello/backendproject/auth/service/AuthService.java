package hello.backendproject.auth.service;

import hello.backendproject.auth.dto.LoginRequestDTO;
import hello.backendproject.auth.dto.SignUpRequestDTO;
import hello.backendproject.user.dto.UserDTO;
import hello.backendproject.user.dto.UserProfileDTO;
import hello.backendproject.user.entity.User;
import hello.backendproject.user.entity.UserProfile;
import hello.backendproject.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public void signUp(SignUpRequestDTO dto){
        if(userRepository.findByUserid(dto.getUserid()).isPresent()) {
            throw new RuntimeException("사용자가 이미 존재합니다.");
        }

        User user = new User();
        user.setUserid(dto.getUserid());
        user.setPassword(dto.getPassword());

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

    @Transactional
    public UserDTO login(LoginRequestDTO dto){
        // 아이디 검사
        User user = userRepository.findByUserid(dto.getUserid()).orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        // 비밀번호 일치 검사
        if(!dto.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 반환할 userDTO 생성
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUserid(user.getUserid());

        // userDTO에 들어갈 userProfileDTO 생성
        UserProfileDTO  profileDTO = new UserProfileDTO();
        profileDTO.setUsername(user.getUserProfile().getUsername());
        profileDTO.setEmail(user.getUserProfile().getEmail());
        profileDTO.setPhone(user.getUserProfile().getPhone());
        profileDTO.setAddress(user.getUserProfile().getAddress());

        userDTO.setProfile(profileDTO);

        return userDTO;
    }

}
