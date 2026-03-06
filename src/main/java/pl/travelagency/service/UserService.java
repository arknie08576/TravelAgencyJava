package pl.travelagency.service;

import org.springframework.stereotype.Service;
import pl.travelagency.auth.PasswordService;
import pl.travelagency.db.UserEntity;
import pl.travelagency.db.UserRole;
import pl.travelagency.dto.AddUserRequest;
import pl.travelagency.dto.PutUserByIdRequest;
import pl.travelagency.dto.UserDto;
import pl.travelagency.repo.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public UserService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public Optional<UserDto> getByLogin(String login) {
        return userRepository.findByLogin(login).map(this::mapToDto);
    }

    public Optional<UserEntity> getEntityByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public boolean loginExists(String login) {
        return userRepository.existsByLogin(login);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserDto addUser(AddUserRequest request) {
        UserEntity entity = new UserEntity();
        entity.setName(request.getName());
        entity.setSurname(request.getSurname());
        entity.setLogin(request.getLogin());
        entity.setEmail(request.getEmail());
        entity.setPassword(passwordService.hashPassword(request.getPassword()));

        if ("admin".equalsIgnoreCase(request.getRole())) {
            entity.setRole(UserRole.admin);
        } else {
            entity.setRole(UserRole.user);
        }

        UserEntity saved = userRepository.save(entity);
        return mapToDto(saved);
    }

    public Optional<UserDto> authenticate(String username, String password) {
        return userRepository.findByLogin(username)
                .filter(user -> passwordService.matches(password, user.getPassword()))
                .map(this::mapToDto);
    }

    public Optional<UserDto> update(Integer userId, PutUserByIdRequest request) {
        return userRepository.findById(userId)
                .map(entity -> {
                    entity.setName(request.getName());
                    entity.setSurname(request.getSurname());
                    entity.setLogin(request.getLogin());
                    entity.setEmail(request.getEmail());

                    if (request.getPassword() != null && !request.getPassword().isBlank()) {
                        entity.setPassword(passwordService.hashPassword(request.getPassword()));
                    }

                    if ("admin".equalsIgnoreCase(request.getRole())) {
                        entity.setRole(UserRole.admin);
                    } else if (request.getRole() != null) {
                        entity.setRole(UserRole.user);
                    }

                    return mapToDto(userRepository.save(entity));
                });
    }

    public Optional<UserDto> deleteById(Integer userId, UserEntity currentUser) {
        if (currentUser == null || currentUser.getRole() == UserRole.user) {
            return Optional.empty();
        }

        return userRepository.findById(userId)
                .map(entity -> {
                    UserDto dto = mapToDto(entity);
                    userRepository.delete(entity);
                    return dto;
                });
    }

    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public UserDto mapToDto(UserEntity entity) {
        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSurname(entity.getSurname());
        dto.setLogin(entity.getLogin());
        dto.setEmail(entity.getEmail());
        dto.setRole(entity.getRole() == UserRole.admin ? 0 : 1);
        return dto;
    }
}