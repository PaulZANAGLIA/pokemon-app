package com.paulz.user.security;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final SecurityJwtProperties jwtProperties;
    private static final int VALIDITY_DAYS = SecurityJwtProperties.jwtValidityDays;
    private final ConcurrentHashMap<String, Boolean> invalidatedTokens = new ConcurrentHashMap<>();

    // Vos autres méthodes existantes (generateToken, decodeToken, convertJwtToPrincipal) ici...

    public void invalidateToken(String token) {
        invalidatedTokens.put(token, true);
    }

    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.containsKey(token);
    }
    
    public boolean isTokenExpired(String token){
        try{
            JWT
            .require(Algorithm.HMAC256(jwtProperties.getSecret()))
            .build()
            .verify(token);
            return false;
        } catch (TokenExpiredException tee) {
            return true;
        }
    }

    public String generateToken(UserPrincipal principal){
        return JWT
            .create()
            .withSubject(String.valueOf(principal.getUserId()))
            .withExpiresAt(Instant.now().plus(Duration.ofDays(VALIDITY_DAYS)))
            .withClaim("email", principal.getEmail())
            .withClaim("username", principal.getUsername())
            .withClaim("roles", principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
            .sign(Algorithm.HMAC256(jwtProperties.getSecret()));
    }

    public DecodedJWT decodeToken(String token){
        if(this.isTokenInvalidated(token)){
            throw new TokenExpiredException("Invalid Token", null);
        }
        if(this.isTokenExpired(token)){
            this.invalidatedTokens.remove(token);
        }
        return JWT
            .require(Algorithm.HMAC256(jwtProperties.getSecret()))
            .build()
            .verify(token);
    }

    public UserPrincipal convertJwtToPrincipal(DecodedJWT jwt){
        return UserPrincipal.builder()
            .userId(Long.valueOf(jwt.getSubject()))
            .email(jwt.getClaim("email").asString())
            .username(jwt.getClaim("username").asString())
            .authorities(extractAuthoritiesFromClaim(jwt))
            .build();
    }

    private List<SimpleGrantedAuthority> extractAuthoritiesFromClaim(DecodedJWT jwt){
        var claim = jwt.getClaim("roles");
        if (claim.isNull() || claim.isMissing()) return List.of();
        return claim.asList(SimpleGrantedAuthority.class);
    }
}
