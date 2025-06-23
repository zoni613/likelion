package hello.backendproject.user.repository;

import hello.backendproject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUserid(String userid);

    List<User> findByUseridAndPassword(String userid, String password);
}
