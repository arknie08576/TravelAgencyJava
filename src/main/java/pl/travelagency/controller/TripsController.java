package pl.travelagency.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.travelagency.api.ErrorType;
import pl.travelagency.api.ResponseBase;
import pl.travelagency.db.TripEntity;
import pl.travelagency.repo.TripRepository;

import java.util.List;

@RestController
@RequestMapping("/Trips")
public class TripsController {

    private final TripRepository repo;

    public TripsController(TripRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public ResponseBase<List<TripEntity>> getAll() {
        try {
            return ResponseBase.ok(repo.findAll());
        } catch (Exception e) {
            return ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR);
        }
    }
}