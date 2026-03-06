package pl.travelagency.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.travelagency.api.ErrorType;
import pl.travelagency.api.ResponseBase;
import pl.travelagency.dto.AddReservationRequest;
import pl.travelagency.dto.PutReservationByIdRequest;
import pl.travelagency.dto.ReservationDto;
import pl.travelagency.service.ReservationService;
import pl.travelagency.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/Reservations")
public class ReservationsController {

    private final ReservationService reservationService;
    private final UserService userService;

    public ReservationsController(
            ReservationService reservationService,
            UserService userService
    ) {
        this.reservationService = reservationService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ResponseBase<List<ReservationDto>>> getAllReservations() {
        try {
            return ResponseEntity.ok(ResponseBase.ok(reservationService.getAll()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ResponseBase<ReservationDto>> getById(@PathVariable Integer reservationId) {
        try {
            return reservationService.getById(reservationId)
                    .map(reservation -> ResponseEntity.ok(ResponseBase.ok(reservation)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ResponseBase.fail(ErrorType.NOT_FOUND)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseBase<ReservationDto>> addReservation(
            @RequestBody AddReservationRequest request,
            Authentication authentication
    ) {
        try {
            var currentUser = authentication == null ? null :
                    userService.getEntityByLogin(authentication.getName()).orElse(null);

            return reservationService.add(request, currentUser)
                    .map(reservation -> ResponseEntity.ok(ResponseBase.ok(reservation)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(ResponseBase.fail(ErrorType.UNAUTHORIZED)));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ResponseBase<ReservationDto>> deleteById(
            @PathVariable Integer reservationId,
            Authentication authentication
    ) {
        try {
            var currentUser = authentication == null ? null :
                    userService.getEntityByLogin(authentication.getName()).orElse(null);

            return reservationService.deleteById(reservationId, currentUser)
                    .map(reservation -> ResponseEntity.ok(ResponseBase.ok(reservation)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(ResponseBase.fail(ErrorType.UNAUTHORIZED)));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<ResponseBase<ReservationDto>> putById(
            @PathVariable Integer reservationId,
            @RequestBody PutReservationByIdRequest request,
            Authentication authentication
    ) {
        try {
            request.setReservationId(reservationId);

            var currentUser = authentication == null ? null :
                    userService.getEntityByLogin(authentication.getName()).orElse(null);

            return reservationService.update(request, currentUser)
                    .map(reservation -> ResponseEntity.ok(ResponseBase.ok(reservation)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(ResponseBase.fail(ErrorType.UNAUTHORIZED)));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }
}