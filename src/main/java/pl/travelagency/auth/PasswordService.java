package pl.travelagency.auth;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class PasswordService {

    private static final int ITERATIONS = 10_000;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int SALT_LENGTH = 16;

    public String hashPassword(String rawPassword) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            new SecureRandom().nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();

            String saltB64 = Base64.getEncoder().encodeToString(salt);
            String hashB64 = Base64.getEncoder().encodeToString(hash);

            return saltB64 + ";" + hashB64;
        } catch (Exception e) {
            throw new IllegalStateException("Password hashing error", e);
        }
    }

    public boolean matches(String rawPassword, String storedPassword) {
        try {
            if (storedPassword == null || !storedPassword.contains(";")) {
                return false;
            }

            String[] parts = storedPassword.split(";", 2);
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String expectedHash = parts[1];

            PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] actualHashBytes = skf.generateSecret(spec).getEncoded();
            String actualHash = Base64.getEncoder().encodeToString(actualHashBytes);

            return MessageDigest.isEqual(expectedHash.getBytes(), actualHash.getBytes());
        } catch (Exception e) {
            return false;
        }
    }
}