package com.paulz.user.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
    private long id;
    private String email;
    private String username;
    @JsonIgnore
    private String password;
    private int elo;
    private Set<String> roles;
    private Set<Long> friends;
}
