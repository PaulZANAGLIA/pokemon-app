package com.paulz.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paulz.user.dto.UserDto;
import com.paulz.user.model.LoginRequest;
import com.paulz.user.model.RegisterRequest;
import com.paulz.user.security.JwtUtil;
import com.paulz.user.security.UserPrincipal;
import com.paulz.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    public void init() {
        userDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .elo(0)
                .roles(Set.of("MEMBER"))
                .friends(Set.of())
                .build();

        registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .username("testuser")
                .password("password")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();
    }

    @Test
    public void testRegisterUser_ShouldRegisterUser() throws Exception {
        when(userService.registerUser(Mockito.any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.username").value(userDto.getUsername()));
    }

    @Test
    public void testRegisterExistingUser_ShouldNotRegisterUser() throws Exception {
        when(userService.registerUser(Mockito.any(UserDto.class))).thenThrow(new IllegalArgumentException("User already exists"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLoginUser_ShouldReturnJwtToken() throws Exception {
        UserPrincipal principal = UserPrincipal.builder()
        .email(userDto.getEmail())
        .email(userDto.getUsername())
        .authorities(userDto.getRoles().stream().map(r -> new SimpleGrantedAuthority(r)).toList())
        .userId(1L)
        .build();
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtUtil.generateToken(Mockito.any())).thenReturn("mocked_jwt_token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mocked_jwt_token"));
    }

    @Test
    public void testBadLoginUser_ShouldNotReturnJwtToken() throws Exception {
        //Given
        // Nothing to given 

        // When & Then
        when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad Logins."));

        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testLogoutUser_ShouldInvalidateToken() throws Exception {
        String validToken = "valid_mocked_jwt_token";

        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));

        // Verify that the token invalidation method was called
        Mockito.verify(jwtUtil).invalidateToken(validToken);
    }

}
