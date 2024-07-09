package com.paulz.user.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulz.user.entity.User;
import com.paulz.user.service.UserService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable long id, @Valid @RequestBody User user) {
       User updatedUser = userService.updateUser(id, user);
       return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable long id) {
        if(userService.deleteUser(id)) return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping()
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(this.userService.getUsers());
    }
    
    @GetMapping("/search/{username}")
    public ResponseEntity<List<User>> getUsersByUsername(@PathVariable String username) {
        List<User> users = userService.getUsersByUsername(username);
        return ResponseEntity.ok(users);
    }
}
