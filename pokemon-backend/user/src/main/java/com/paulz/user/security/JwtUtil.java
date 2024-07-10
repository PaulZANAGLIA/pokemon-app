package com.paulz.user.security;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final SecurityJwtProperties jwtProperties;
    private static final int VALIDITY_DAYS = SecurityJwtProperties.jwtValidityDays;

    public String generateToken(long userId, String email, List<String> roles){
        return JWT
            .create()
            .withSubject(String.valueOf(userId))
            .withExpiresAt(Instant.now().plus(Duration.ofDays(VALIDITY_DAYS)))
            .withClaim("email", email)
            .withClaim("roles", roles)
            .sign(Algorithm.HMAC256(jwtProperties.getSecret()));
    }

    public DecodedJWT decodeToken(String token){
        return JWT
            .require(Algorithm.HMAC256(jwtProperties.getSecret()))
            .build()
            .verify(token);
    }

    public UserPrincipal convertJwtToPrincipal(DecodedJWT jwt){
        return UserPrincipal.builder()
            .userId(Long.valueOf(jwt.getSubject()))
            .email(jwt.getClaim("email").asString())
            .authorities(extractAuthoritiesFromClaim(jwt))
            .build();
    }

    private List<SimpleGrantedAuthority> extractAuthoritiesFromClaim(DecodedJWT jwt){
        var claim = jwt.getClaim("roles");
        if (claim.isNull() || claim.isMissing()) return List.of();
        return claim.asList(SimpleGrantedAuthority.class);
    }
}
