package com.paulz.user.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties("security.jwt")
public class SecurityJwtProperties {
    public final static int jwtValidityDays = 1;
    private String secret;
}
