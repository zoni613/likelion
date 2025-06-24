package hello.backendproject.auth.entity;

import hello.backendproject.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity // DB 테이블과 자바 객체를 연결
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // 테이블의 컬럼을 자바 필드와 연결
    private String tokenType;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    //테이블과 테이블을 연결      (1대1 관계에서는 연관관계 주인쪽만 패치 전략이 적용됨)
    @OneToOne(fetch = FetchType.LAZY) // 지연로딩 -> Auth 엔티티 조회할 때 user 객체는 불러오지 않음
    @JoinColumn(name = "user_id") // auth.getUser()에 실제로 접근할 때 User 쿼리 발생!
    private User user;
}
