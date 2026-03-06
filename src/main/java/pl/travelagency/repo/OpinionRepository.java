package pl.travelagency.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.travelagency.db.OpinionEntity;

public interface OpinionRepository extends JpaRepository<OpinionEntity, Integer> {
    boolean existsByReservation_Id(Integer reservationId);
}