package com.paulz.user.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulz.user.entity.Role;
import com.paulz.user.entity.User;
import com.paulz.user.model.LoginRequest;
import com.paulz.user.model.LoginResponse;
import com.paulz.user.model.RegisterRequest;
import com.paulz.user.security.JwtUtil;
import com.paulz.user.service.RoleService;
import com.paulz.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;
    

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest userForm) {
        try {
            User user = new User();
            user.setUsername(userForm.getUsername());
            user.setEmail(userForm.getEmail());
            user.setPassword(userForm.getPassword());

            HashSet<Role> roles = new HashSet<>();
            roles.add(this.roleService.getRoleByName("MEMBER"));
            user.setRoles(roles);

            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        Optional<User> loggedUser = userService.loginUser(request.getEmail(), request.getPassword());
        List<String> roles = loggedUser.map(user -> user.getRoles().stream().map(r -> r.getName()).toList()).orElseGet(List::of);
        return loggedUser
            .map(user ->  ResponseEntity.ok(
                LoginResponse
                    .builder()
                    .accessToken(
                        jwtUtil.generateToken(user.getId(), user.getEmail(), roles)
                    ).build()
            ))
            .orElseGet(() -> ResponseEntity.status(401).build());
    }
}
