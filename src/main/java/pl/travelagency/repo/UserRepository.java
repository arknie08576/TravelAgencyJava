package pl.travelagency.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.travelagency.db.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {}