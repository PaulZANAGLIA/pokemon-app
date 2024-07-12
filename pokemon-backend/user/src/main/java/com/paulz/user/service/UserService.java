package com.paulz.user.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.paulz.user.dto.UserDto;
import com.paulz.user.entity.Role;
import com.paulz.user.entity.User;
import com.paulz.user.repository.RoleRepository;
import com.paulz.user.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository  roleRepository;
    private final PasswordEncoder passwordEncoder;
    static final int MAX_FRIENDS = 30;

    public UserDto convertToDto(User user){
        return UserDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .roles(user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()))
            .friends(user.getFriends().stream().map(f -> f.getId()).collect(Collectors.toSet()))
            .build();
    }

    public User convertToEntity(UserDto userDto){
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setPassword(this.passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setRoles(userDto.getRoles().stream().map(roleRepository::findByName).collect(Collectors.toSet()));
        user.setFriends(userDto.getFriends().stream().map(userRepository::findById).map(Optional::get).collect(Collectors.toSet()));
        return user;
    }

    public Set<Role> getUserRoleById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found, id:" + userId));
        return user.getRoles(); // Supposons que la classe User a une méthode getRole() qui renvoie le rôle de l'utilisateur
    }

    public UserDto registerUser(@Valid UserDto userDto){
        User user = convertToEntity(userDto);
        User savedUser = this.userRepository.save(user);
        return convertToDto(savedUser);
    }

    public UserDto updateUser(long id, UserDto userDto){
        User user = this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found."));
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setElo(userDto.getElo());
        user.setRoles(userDto.getRoles().stream().map(roleRepository::findByName).collect(Collectors.toSet()));
        user.setFriends(userDto.getFriends().stream().map(userRepository::findById).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet()));

        if(userDto.getPassword() != null && !userDto.getPassword().isEmpty()){
            user.setPassword(this.passwordEncoder.encode(userDto.getPassword()));
        }

        User updatedUser = this.userRepository.save(user);
        return convertToDto(updatedUser);
    }

    public boolean deleteUser(long id){
        Optional<User> optionalUser = this.userRepository.findById(id);
        if(optionalUser.isPresent()) {
            this.userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<UserDto> getUsers(){
        return this.userRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public Optional<UserDto> getUserById(long id) {
        return this.userRepository.findById(id).map(this::convertToDto);
    }

    public Optional<UserDto> getUserByEmail(String email) {
        return this.userRepository.findByEmail(email).map(this::convertToDto);
    }

    public List<UserDto> getUsersByUsername(String username) {
        return this.userRepository.findByUsername(username).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /* FRIENDS METHODES */
    
    public List<UserDto> getFriends(long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
    
        if (!userOpt.isPresent())
            throw new jakarta.persistence.EntityNotFoundException("User not found.");
    
        User user = userOpt.get();
    
        return user.getFriends().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void addFriend(long userId, long friendId) {
        // Fetch users from database
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        User friend = userRepository.findById(friendId).orElseThrow(() -> new RuntimeException("User not found with id: " + friendId));

        if(user.getFriends().size() >= MAX_FRIENDS || friend.getFriends().size() >= MAX_FRIENDS)
            throw new RuntimeException("User" + userId + " or User" + friendId + " has to many friends (max is "+ MAX_FRIENDS + "+)");
        
        // Add friend relation bidirectionally
        user.getFriends().add(friend);
        friend.getFriends().add(user);

        // Save changes
        userRepository.save(user);
        userRepository.save(friend);
    }
    
    public void removeFriend(long userId, long friendId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<User> friendOpt = userRepository.findById(friendId);
    
        if (!(userOpt.isPresent() && friendOpt.isPresent()))
            throw new jakarta.persistence.EntityNotFoundException("User or friend not found.");
    
        // biderectional
        User user = userOpt.get();
        User friend = friendOpt.get();
        
        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
        userRepository.save(user);
        userRepository.save(friend);
    }
    
}
