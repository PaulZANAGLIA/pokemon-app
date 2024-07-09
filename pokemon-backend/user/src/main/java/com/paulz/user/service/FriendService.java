package com.paulz.user.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paulz.user.model.User;
import com.paulz.user.repository.UserRepository;

@Service
public class FriendService {
    private static final int MAX_FRIENDS = 30;

    @Autowired
    private UserRepository userRepository;

    public void addFriend(long userId, long friendId){
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<User> friendOpt = userRepository.findById(friendId);

        if(!(userOpt.isPresent() && friendOpt.isPresent()))
            throw new jakarta.persistence.EntityNotFoundException("User or friend not found.");
            
        User user = userOpt.get();
        User friend = friendOpt.get();

        if(user.getFriends().size() >= MAX_FRIENDS)
            throw new jakarta.persistence.EntityNotFoundException("Friend list is full (max 30 friends).");
        
        user.getFriends().add(friend);
        userRepository.save(user);
    }

    public void removeFriend(long userId, long friendId){
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<User> friendOpt = userRepository.findById(friendId);

        if(!(userOpt.isPresent() && friendOpt.isPresent()))
            throw new jakarta.persistence.EntityNotFoundException("User or friend not found.");

        User user = userOpt.get();
        User friend = friendOpt.get();
    
        user.getFriends().remove(friend);
        userRepository.save(user);
    }

    public Set<User> getFriends(long userId){
        Optional<User> userOpt = userRepository.findById(userId);

        if(!userOpt.isPresent())
            throw new jakarta.persistence.EntityNotFoundException("User not found.");
        
        User user = userOpt.get();
        return user.getFriends();
    }


}
