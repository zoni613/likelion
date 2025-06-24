package hello.backendproject.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    // user와 userProfile Entity가 다르기 때문에 분리해서 DTO 생성
    private String username;
    private String email;
    private String phone;
    private String address;

}
