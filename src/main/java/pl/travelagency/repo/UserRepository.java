package pl.travelagency.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.travelagency.db.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByLogin(String login);
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
}