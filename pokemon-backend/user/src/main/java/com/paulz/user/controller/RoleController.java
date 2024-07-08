package com.paulz.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulz.user.service.RoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    
    @Autowired
    private RoleService roleService;

    @PostMapping()
    public ResponseEntity<Void> createRole(@Valid @RequestBody String role) {
        this.roleService.createRole(role);
        return ResponseEntity.ok(null);
    }
}
