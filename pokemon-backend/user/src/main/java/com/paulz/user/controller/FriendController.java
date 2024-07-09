package com.paulz.user.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulz.user.model.User;
import com.paulz.user.service.FriendService;

@RestController
@RequestMapping("/api/friends")
public class FriendController {
    @Autowired
    private FriendService friendService;

    @GetMapping("/{userId}")
    public ResponseEntity<Set<User>> getFriends(@PathVariable long userId) {
        return ResponseEntity.ok(this.friendService.getFriends(userId));
    }

    @DeleteMapping("/{userId}/{friendId}")
    public ResponseEntity<String> removeFriend(@PathVariable long userId, @PathVariable long friendId) {
        try {
            friendService.removeFriend(userId, friendId);
            return ResponseEntity.ok("Friend removed successfully!");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable long userId, @PathVariable long friendId) {
        try {
            friendService.addFriend(userId, friendId);
            Set<User> friends = friendService.getFriends(userId);
            return ResponseEntity.ok(friends);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
}
