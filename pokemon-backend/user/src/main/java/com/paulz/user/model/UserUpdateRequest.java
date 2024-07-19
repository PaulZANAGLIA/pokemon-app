package com.paulz.user.model;

import java.util.Set;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class UserUpdateRequest {
    private long id;
    private String email;
    private String username;
    private String password;
    private int elo;
    private Set<String> roles;
    private Set<Long> friends;
}

