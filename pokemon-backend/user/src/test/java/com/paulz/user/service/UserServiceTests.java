package com.paulz.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.paulz.user.dto.UserDto;
import com.paulz.user.entity.Role;
import com.paulz.user.entity.User;
import com.paulz.user.repository.RoleRepository;
import com.paulz.user.repository.UserRepository;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private User user;
    private Role role;

    @BeforeEach
    public void init(){
        role = Role.builder().name("MEMBER").build();

        userDto = UserDto.builder()
        .id(1L)
        .username("user")
        .password("password")
        .email("user@example.com")
        .elo(0)
        .roles(Set.of("MEMBER"))
        .friends(Set.of())
        .build();

        user = new User();
        user.setUsername("user");
        user.setPassword("encodedPassword");
        user.setEmail("user@example.com");
        user.setElo(0);
        user.setRoles(Set.of(Role.builder().name("MEMBER").build()));
        user.setId(1L);
    }

    @Test
    public void testGetUserRoleById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Set<Role> roles = userService.getUserRoleById(1L);
        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertTrue(roles.stream().anyMatch(r -> "MEMBER".equals(r.getName())));
    }

    @Test
    public void testRegisterUser() {
        when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn("encodedPassword");
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        UserDto savedUserDto = userService.registerUser(userDto);
        assertNotNull(savedUserDto);
        assertEquals("user", savedUserDto.getUsername());
        assertEquals("user@example.com", savedUserDto.getEmail());
    }

    @Test
    public void testUpdateUser() {
        UserDto updateUserDto = UserDto.builder()
        .id(1L)
        .username("updateuser")
        .password("encodedNewPassword")
        .email("user_new_mail@example.com")
        .elo(1000)
        .roles(Set.of("MEMBER"))
        .friends(Set.of())
        .build();

        Role role = Role.builder().name("MEMBER").build();

        User updatedUser = new User();
        updatedUser.setUsername("updateuser");
        updatedUser.setPassword("encodedNewPassword");
        updatedUser.setEmail("user_new_mail@example.com");
        updatedUser.setElo(1000);
        updatedUser.setRoles(Set.of(role));
        updatedUser.setId(1L);

        when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn("encodedNewPassword");
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(updatedUser));
        when(roleRepository.findByName(Mockito.any())).thenReturn(role);
        when(userRepository.save(Mockito.any())).thenReturn(updatedUser);

        UserDto updatedSavedUserDto = userService.updateUser(1L, updateUserDto);
        assertNotNull(updatedSavedUserDto);
        assertEquals("updateuser", updatedSavedUserDto.getUsername());
        assertEquals("user_new_mail@example.com", updatedSavedUserDto.getEmail());
    }

    @Test
    public void testDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertTrue(userService.deleteUser(1L));
    }

    @Test
    public void testGetUsers() {
        User user2 = new User();
        user2.setUsername("Jane Doe");
        user2.setEmail("jane.doe@example.com");
        user2.setElo(0);
        user2.setPassword("mygreatpassword");
        user2.setRoles(Set.of(role));

        List<User> users = Arrays.asList(user, user2);

        when(userRepository.findAll()).thenReturn(users);
        
        List<UserDto> userDtos = userService.getUsers();

        assertNotNull(userDtos);
        assertEquals(2, userDtos.size());
        assertEquals("user", userDtos.get(0).getUsername());
        assertEquals("Jane Doe", userDtos.get(1).getUsername());
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserDto> userDtoOpt = userService.getUserById(1L);

        assertTrue(userDtoOpt.isPresent());
        assertEquals("user", userDtoOpt.get().getUsername());
    }

    @Test
    public void testGetUserByEmail() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Optional<UserDto> userDtoOpt = userService.getUserByEmail("user@example.com");

        assertTrue(userDtoOpt.isPresent());
        assertEquals("user", userDtoOpt.get().getUsername());
    }

    @Test
    public void testGetUsersByUsername() {
        User user2 = new User();
        user2.setUsername("user");
        user2.setEmail("jane.doe@example.com");
        user2.setElo(0);
        user2.setPassword("mygreatpassword");
        user2.setRoles(Set.of(role));

        List<User> users = Arrays.asList(user, user2);

        when(userRepository.findByUsername("user")).thenReturn(users);

        List<UserDto> userDtos = userService.getUsersByUsername("user");

        assertNotNull(userDtos);
        assertEquals(2, userDtos.size());
        assertEquals("user@example.com", userDtos.get(0).getEmail());
        assertEquals("jane.doe@example.com", userDtos.get(1).getEmail());
    }
    
    @Test
    public void testGetFriends() {
        User friend = new User();
        friend.setUsername("Jane Doe");
        friend.setEmail("jane.doe@example.com");
        friend.setElo(7);
        friend.setPassword("mygreatpassword");
        friend.setRoles(Set.of(role));
        friend.setFriends(Set.of(user));

        user.setFriends(Set.of(friend));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<UserDto> friendsDto = userService.getFriends(1L);
        assertNotNull(friendsDto);
        assertEquals(1, friendsDto.size());
    }

    @Test
    public void testAddFriend() {
        User friend = new User();
        friend.setRoles(Set.of(role));
        friend.setFriends(new HashSet<>());
        friend.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(friend));

        userService.addFriend(1L, 2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        List<UserDto> friendsDto = userService.getFriends(1L);

        assertNotNull(friendsDto);
        assertEquals(1, friendsDto.size());
        assertEquals(2L, friendsDto.get(0).getId());
        assertEquals(1, friendsDto.get(0).getFriends().size());
        assertTrue(friendsDto.get(0).getFriends().contains(1L));
    }

    @Test
    public void testAddFriend_UserHasMaxFriends() {
        User user = new User();
        user.setId(1L);
        user.setFriends(new HashSet<>());

        User friend = new User();
        friend.setId(2L);
        friend.setFriends(new HashSet<>());

        for (int i = 0; i < UserService.MAX_FRIENDS; i++) {
            User u = new User();
            u.setId(3L + i);
            user.getFriends().add(u);
        }

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(friend));

        assertThrows(RuntimeException.class, () -> userService.addFriend(1L, 2L));
    }

    @Test
    public void testRemoveFriend() {
        User user = new User();
        user.setId(1L);

        User friend = new User();
        friend.setId(2L);

        user.setFriends(new HashSet<>(Arrays.asList(friend)));
        friend.setFriends(new HashSet<>(Arrays.asList(user)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(friend));

        userService.removeFriend(1L, 2L);

        verify(userRepository).save(user);
        verify(userRepository).save(friend);

        assertTrue(user.getFriends().isEmpty());
        assertTrue(friend.getFriends().isEmpty());
    }
    
}




