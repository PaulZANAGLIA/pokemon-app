package com.paulz.user.utility;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.paulz.user.model.Role;

@Component
public class JwtUtil {
    private static final String SECRET = "my-pretty-secret";
    private static final int VALIDITY_DAYS = 15;
    public String generateToken(long userId, String email, Collection<Role> collectionRoles){
        List<String> roles = collectionRoles.stream().map(r -> r.getName()).toList();
        return JWT
            .create()
            .withSubject(String.valueOf(userId))
            .withExpiresAt(Instant.now().plus(Duration.ofDays(VALIDITY_DAYS)))
            .withClaim("email", email)
            .withClaim("roles", roles)
            .sign(Algorithm.HMAC256(SECRET));
    }
}
