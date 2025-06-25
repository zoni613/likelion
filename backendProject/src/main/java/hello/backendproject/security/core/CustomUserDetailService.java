package hello.backendproject.security.core;

import hello.backendproject.user.entity.User;
import hello.backendproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;


    // 로그인할 때 스프링에서 DB에 현재 로그인하는 사용자가 존재하는지 확인하는 메서드
    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        User user = userRepository.findByUserid(userid).orElseThrow(
                () -> new UsernameNotFoundException("User not found with userid" + userid));
        return new CustomUserDetails(user);
    }

    public UserDetails loadUserById(Long userid) throws UsernameNotFoundException {
        User user = userRepository.findById(userid).orElseThrow(
                () -> new UsernameNotFoundException("User not found with userid" + userid));
        return new CustomUserDetails(user);
    }


}
