package com.paulz.user.controller;

import java.util.HashSet;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulz.user.model.LoginRequest;
import com.paulz.user.model.LoginResponse;
import com.paulz.user.model.RegisterRequest;
import com.paulz.user.model.Role;
import com.paulz.user.model.User;
import com.paulz.user.service.RoleService;
import com.paulz.user.service.UserService;
import com.paulz.user.utility.JwtUtil;

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
    public ResponseEntity<User> registerUser(@Valid @RequestBody RegisterRequest userForm) {
        System.out.println(userForm);
        User user = new User();
        user.setUsername(userForm.getUsername());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());

        HashSet<Role> roles = new HashSet<>();
        roles.add(this.roleService.getRoleByName("MEMBER"));
        user.setRoles(roles);

        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        Optional<User> loggedUser = userService.loginUser(request.getEmail(), request.getPassword());
        return loggedUser
            .map(user ->  ResponseEntity.ok(
                LoginResponse
                    .builder()
                    .accessToken(
                        jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRoles())
                    ).build()
            ))
            .orElseGet(() -> ResponseEntity.status(401).build());
    }
}
