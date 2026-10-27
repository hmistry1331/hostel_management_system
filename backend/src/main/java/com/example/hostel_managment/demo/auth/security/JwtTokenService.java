package com.example.hostel_managment.demo.auth.security;

import com.example.hostel_managment.demo.auth.domain.UserRole;
import com.example.hostel_managment.demo.auth.domain.AppUser;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private final byte[] secret;
    private final long expirationSeconds;

    public JwtTokenService(
            @Value("${app.auth.jwt-secret:change-this-development-secret-to-a-long-random-value}") String secret,
            @Value("${app.auth.jwt-expiration-seconds:3600}") long expirationSeconds) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationSeconds = expirationSeconds;
        if (secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 characters");
        }
    }

    public String create(AppUser user, Long workerId) {
        long expiresAt = Instant.now().plusSeconds(expirationSeconds).getEpochSecond();
        String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = encode("{\"sub\":" + user.getId() + ",\"role\":\"" + user.getRole()
                + "\",\"name\":\"" + escape(user.getName()) + "\",\"workerId\":"
                + (workerId == null ? "null" : workerId) + ",\"exp\":" + expiresAt + "}");
        String unsigned = header + "." + payload;
        return unsigned + "." + sign(unsigned);
    }

    public AuthenticatedUser parse(String token) {
        try {
            String[] parts = token.split("\\.", -1);
            if (parts.length != 3 || !MessageDigest.isEqual(parts[2].getBytes(StandardCharsets.UTF_8),
                    sign((parts[0] + "." + parts[1])).getBytes(StandardCharsets.UTF_8))) {
                return null;
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            long userId = number(payload, "sub");
            long expiration = number(payload, "exp");
            if (Instant.now().getEpochSecond() >= expiration) {
                return null;
            }
            String role = text(payload, "role");
            String name = text(payload, "name");
            Long workerId = nullableNumber(payload, "workerId");
            return new AuthenticatedUser(userId, workerId, UserRole.valueOf(role), name);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(
                    mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to sign authentication token", exception);
        }
    }

    private String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private long number(String json, String key) {
        return Long.parseLong(json.replaceAll(".*\"" + key + "\":([0-9]+).*", "$1"));
    }

    private Long nullableNumber(String json, String key) {
        String value = json.replaceAll(".*\"" + key + "\":(null|[0-9]+).*", "$1");
        return "null".equals(value) ? null : Long.valueOf(value);
    }

    private String text(String json, String key) {
        return json.replaceAll(".*\"" + key + "\":\"([^\"]*)\".*", "$1");
    }
}
