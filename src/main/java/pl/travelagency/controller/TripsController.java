package pl.travelagency.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.travelagency.api.ErrorType;
import pl.travelagency.api.ResponseBase;
import pl.travelagency.dto.TripDto;
import pl.travelagency.service.TripService;

import java.util.List;

@RestController
@RequestMapping("/Trips")
public class TripsController {

    private final TripService tripService;

    public TripsController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public ResponseEntity<ResponseBase<List<TripDto>>> getAll() {
        try {
            return ResponseEntity.ok(ResponseBase.ok(tripService.getAll()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBase<TripDto>> getById(@PathVariable Integer id) {
        try {
            return tripService.getById(id)
                    .map(trip -> ResponseEntity.ok(ResponseBase.ok(trip)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ResponseBase.fail(ErrorType.NOT_FOUND)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }
}