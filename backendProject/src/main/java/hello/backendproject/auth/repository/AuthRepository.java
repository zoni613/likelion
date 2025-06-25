package hello.backendproject.auth.repository;

import hello.backendproject.auth.entity.Auth;
import hello.backendproject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {

    boolean existsByUser(User user);

    Optional<Auth> findByRefreshToken(String refreshToken);

}
