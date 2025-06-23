package hello.backendproject.auth.entity;

import hello.backendproject.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tokenType;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY) // 지연로딩 -> Auth 엔티티 조회할 때 user 객체는 불러오지 않음
    @JoinColumn(name = "user_id") // auth.getUser()에 실제로 접근할 때 User 쿼리 발생!
    private User user;
}
