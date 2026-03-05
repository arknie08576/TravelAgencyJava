package pl.travelagency.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.travelagency.db.TripEntity;

public interface TripRepository extends JpaRepository<TripEntity, Integer> {}