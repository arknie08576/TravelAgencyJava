package pl.travelagency.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.travelagency.db.ReservationEntity;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Integer> {}