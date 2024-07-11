package com.paulz.user.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.paulz.user.entity.User;
import com.paulz.user.repository.UserRepository;
import com.paulz.user.security.UserPrincipal;


@Service
public class UserDetailService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;

    private Optional<User> getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = getUserByEmail(email).orElseThrow();
        return UserPrincipal.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .authorities(user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName())).toList())
            .password(user.getPassword())
            .build();
    }
    
}
