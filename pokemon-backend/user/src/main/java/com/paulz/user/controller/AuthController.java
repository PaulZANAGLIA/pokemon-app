package com.paulz.user.controller;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulz.user.dto.UserDto;
import com.paulz.user.model.LoginRequest;
import com.paulz.user.model.LoginResponse;
import com.paulz.user.model.RegisterRequest;
import com.paulz.user.security.JwtUtil;
import com.paulz.user.security.UserPrincipal;
import com.paulz.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    
    private final UserService userService;

    private final AuthenticationManager authenticationManager;
    

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest userForm) {
        try {
            UserDto user = UserDto
                .builder()
                .email(userForm.getEmail())
                .username(userForm.getUsername())
                .password(userForm.getPassword())
                .elo(0)
                .roles(Set.of("MEMBER"))
                .friends(Set.of())
                .build();
            UserDto registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public LoginResponse loginUser(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(auth);
        var principal = (UserPrincipal) auth.getPrincipal();
        String token = jwtUtil.generateToken(principal.getUserId(), principal.getEmail(), principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        
        return LoginResponse
            .builder()
            .accessToken(token)
            .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Extraction du token JWT sans le préfixe "Bearer "

            // invalider le token JWT
            jwtUtil.invalidateToken(token);

            // Effacer le contexte d'authentification Spring Security
            SecurityContextHolder.clearContext();
        }

        return ResponseEntity.ok("Logged out successfully");
    }


}
