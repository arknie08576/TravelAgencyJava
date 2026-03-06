package pl.travelagency.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import pl.travelagency.auth.PasswordService;

public class CustomPasswordEncoder implements PasswordEncoder {

    private final PasswordService passwordService;

    public CustomPasswordEncoder(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return passwordService.hashPassword(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordService.matches(rawPassword.toString(), encodedPassword);
    }
}