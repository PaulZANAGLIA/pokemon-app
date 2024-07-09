package com.paulz.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.paulz.user.model.User;
import com.paulz.user.repository.UserRepository;

import jakarta.validation.Valid;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(@Valid User user){
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return this.userRepository.save(user);
    }

    public Optional<User> loginUser(String email, String rawPassword){
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(this.passwordEncoder.matches(rawPassword, user.getPassword()))
                return optionalUser;
        }
        return Optional.empty();
    }

    public User updateUser(long id, User userEdit){
        User user = this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found."));
        user.setElo(userEdit.getElo());
        user.setEmail(userEdit.getEmail());
        user.setUsername(userEdit.getUsername());
        user.setRoles(userEdit.getRoles());
        if(!userEdit.getPassword().isEmpty()){
            user.setPassword(this.passwordEncoder.encode(userEdit.getPassword()));
        }
        return this.userRepository.save(user);
    }

    public boolean deleteUser(long id){
        Optional<User> optionalUser = this.userRepository.findById(id);
        if(optionalUser.isPresent()) {
            this.userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<User> getUsers(){
        return this.userRepository.findAll();
    }

    public Optional<User> getUserById(long id) {
        return this.userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public List<User> getUsersByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }
}
