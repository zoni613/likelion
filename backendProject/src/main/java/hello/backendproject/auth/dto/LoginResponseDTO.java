package hello.backendproject.auth.dto;

import hello.backendproject.auth.entity.Auth;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private long userId;

    @Builder
    public LoginResponseDTO(Auth auth) {
        this.tokenType = auth.getTokenType();
        this.accessToken = auth.getAccessToken();
        this.refreshToken = auth.getRefreshToken();
        this.userId = auth.getId();
    }

}
