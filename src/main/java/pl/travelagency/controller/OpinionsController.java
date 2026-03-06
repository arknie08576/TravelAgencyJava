package pl.travelagency.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.travelagency.api.ErrorType;
import pl.travelagency.api.ResponseBase;
import pl.travelagency.dto.AddOpinionRequest;
import pl.travelagency.dto.OpinionDto;
import pl.travelagency.dto.PutOpinionByIdRequest;
import pl.travelagency.service.OpinionService;
import pl.travelagency.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/Opinions")
public class OpinionsController {

    private final OpinionService opinionService;
    private final UserService userService;

    public OpinionsController(
            OpinionService opinionService,
            UserService userService
    ) {
        this.opinionService = opinionService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ResponseBase<List<OpinionDto>>> getAllOpinions() {
        try {
            return ResponseEntity.ok(ResponseBase.ok(opinionService.getAll()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{opinionId}")
    public ResponseEntity<ResponseBase<OpinionDto>> getById(@PathVariable Integer opinionId) {
        try {
            return opinionService.getById(opinionId)
                    .map(opinion -> ResponseEntity.ok(ResponseBase.ok(opinion)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ResponseBase.fail(ErrorType.NOT_FOUND)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseBase<OpinionDto>> addOpinion(
            @RequestBody AddOpinionRequest request,
            Authentication authentication
    ) {
        try {
            var currentUser = authentication == null ? null :
                    userService.getEntityByLogin(authentication.getName()).orElse(null);

            return opinionService.add(request, currentUser)
                    .map(opinion -> ResponseEntity.ok(ResponseBase.ok(opinion)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(ResponseBase.fail(ErrorType.UNAUTHORIZED)));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/{opinionId}")
    public ResponseEntity<ResponseBase<OpinionDto>> deleteById(
            @PathVariable Integer opinionId,
            Authentication authentication
    ) {
        try {
            var currentUser = authentication == null ? null :
                    userService.getEntityByLogin(authentication.getName()).orElse(null);

            return opinionService.deleteById(opinionId, currentUser)
                    .map(opinion -> ResponseEntity.ok(ResponseBase.ok(opinion)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(ResponseBase.fail(ErrorType.UNAUTHORIZED)));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/{opinionId}")
    public ResponseEntity<ResponseBase<OpinionDto>> putById(
            @PathVariable Integer opinionId,
            @RequestBody PutOpinionByIdRequest request,
            Authentication authentication
    ) {
        try {
            request.setOpinionId(opinionId);

            var currentUser = authentication == null ? null :
                    userService.getEntityByLogin(authentication.getName()).orElse(null);

            return opinionService.update(request, currentUser)
                    .map(opinion -> ResponseEntity.ok(ResponseBase.ok(opinion)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(ResponseBase.fail(ErrorType.UNAUTHORIZED)));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }
}