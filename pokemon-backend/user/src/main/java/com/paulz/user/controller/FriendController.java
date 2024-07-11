package com.paulz.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulz.user.dto.UserDto;
import com.paulz.user.service.UserService;

@RestController
@RequestMapping("/api/friends")
public class FriendController {
    @Autowired
    private UserService userService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserDto>> getFriends(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getFriends(userId));
    }

    @DeleteMapping("/{userId}/{friendId}")
    @PreAuthorize(
        "#userId == authentication.principal.userId"
    )
    public ResponseEntity<String> removeFriend(@PathVariable long userId, @PathVariable long friendId) {
        try {
            userService.removeFriend(userId, friendId);
            return ResponseEntity.ok("Friend removed successfully!");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{userId}/{friendId}")
    @PreAuthorize(
        "#userId == authentication.principal.userId"
    )
    public ResponseEntity<?> addFriend(@PathVariable long userId, @PathVariable long friendId) {
        try {
            
            userService.addFriend(userId, friendId);
            List<UserDto> friends = userService.getFriends(userId);
            return ResponseEntity.ok(friends);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
}
