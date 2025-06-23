package hello.backendproject.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// dto: 데이터 전달 객체, Entity의 모든 정보가 아닌 요청에 필요한 정보만 보내줌. 또는 순환 참조를 방지
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDTO {

    private String userid;
    private String password;
    private String username;
    private String email;
    private String phone;
    private String address;

}
