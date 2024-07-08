package com.paulz.user.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterRequest {
    private String email;
    private String username;
    private String password;
}
