package pl.travelagency.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.travelagency.api.ErrorModel;
import pl.travelagency.api.ErrorType;
import pl.travelagency.api.ResponseBase;
import pl.travelagency.dto.AddUserRequest;
import pl.travelagency.dto.AuthenticateUserRequest;
import pl.travelagency.dto.UserDto;
import pl.travelagency.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/Users")
public class UsersController {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ResponseBase<List<UserDto>>> getAll() {
        try {
            return ResponseEntity.ok(ResponseBase.ok(userService.getAll()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseBase<UserDto>> me(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseBase.fail(ErrorType.NOT_AUTHENTICATED));
            }

            return userService.getByLogin(authentication.getName())
                    .map(user -> ResponseEntity.ok(ResponseBase.ok(user)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ResponseBase.fail(ErrorType.NOT_FOUND)));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<ResponseBase<UserDto>> getByUsername(@PathVariable String username) {
        try {
            return userService.getByLogin(username)
                    .map(user -> ResponseEntity.ok(ResponseBase.ok(user)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ResponseBase.fail(ErrorType.NOT_FOUND)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseBase<UserDto>> addUser(@Valid @RequestBody AddUserRequest request) {
        try {
            if (userService.loginExists(request.getLogin())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseBase.fail(ErrorType.LOGIN_ALREADY_EXISTS));
            }

            if (userService.emailExists(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseBase.fail(ErrorType.EMAIL_ALREADY_EXISTS));
            }

            UserDto created = userService.addUser(request);
            return ResponseEntity.ok(ResponseBase.ok(created));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBase.fail(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticateUserRequest request) {
        try {
            return userService.authenticate(request.getUsername(), request.getPassword())
                    .<ResponseEntity<?>>map(user -> ResponseEntity.ok(ResponseBase.ok(user)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ErrorModel(ErrorType.NOT_FOUND)));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorModel(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }
}