package hello.backendproject.user.entity;

import hello.backendproject.auth.entity.Auth;
import hello.backendproject.board.entity.Board;
import hello.backendproject.security.core.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userid;

    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING) // 이 필드를 DB에 문자열로 저장하라는 의미
    private Role role;

    // mappedBy: 나를 참조하고 있는 객체 이름
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile userProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Auth auth;

    // 게시글 OneToMany
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Board> boards = new ArrayList<>();

}
