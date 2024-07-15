package com.paulz.user.controller;

import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.paulz.user.dto.UserDto;
import com.paulz.user.entity.Role;
import com.paulz.user.security.UserPrincipal;
import com.paulz.user.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;
    private UserDto userDto2;
    private UserDto modoDto;
    private UserDto modoDto2;

    @BeforeEach
    public void init(){
        userDto = UserDto.builder()
        .id(1L)
        .username("userUser")
        .email("user@example.com")
        .roles(Set.of("MEMBER"))
        .friends(Set.of())
        .elo(0)
        .build();

        userDto2 = UserDto.builder()
        .id(5L)
        .username("user2User")
        .email("user2@example.com")
        .roles(Set.of("MEMBER"))
        .friends(Set.of())
        .elo(1)
        .build();

        modoDto = UserDto.builder()
        .id(2L)
        .username("modo")
        .email("modo@example.com")
        .roles(Set.of("MODERATOR"))
        .friends(Set.of())
        .elo(100)
        .build();

        modoDto2 = UserDto.builder()
        .id(6L)
        .username("modo2")
        .email("modo2@example.com")
        .roles(Set.of("MODERATOR"))
        .friends(Set.of())
        .elo(101)
        .build();
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    public void testUpdateUser_AsAdmin_ShouldUpdateModoProfile() throws Exception {
        // Given
        long userId = 2L; // This should match the authenticated user's I

        UserDto updatedModoDto = UserDto.builder().id(userId).username("updateUsername").build();

        when(userService.updateUser(userId, modoDto)).thenReturn(updatedModoDto);

        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modoDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"MODERATOR"})
    public void testUpdateUser_AsModo_ShouldUpdateMemberProfile() throws Exception {
        // Given
        long userId = 1L; 

        Set<Role> roles = Set.of(Role.builder().name("MEMBER").build());
        UserDto updatedUserDto = UserDto.builder().id(userId).username("updateUsername").build();
        
        when(userService.getUserRoleById(userId)).thenReturn(roles);
        when(userService.updateUser(userId, userDto)).thenReturn(updatedUserDto);

        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"MODERATOR"})
    public void testUpdateUser_AsModo_ShouldNotUpdateOtherModoProfile() throws Exception {
        // Given
        long userId = 6L; 
        Set<Role> roles = Set.of(Role.builder().name("MODERATOR").build());
        UserDto updatedUserDto = UserDto.builder().id(userId).username("updateUsername").build();
        
        when(userService.getUserRoleById(userId)).thenReturn(roles);
        when(userService.updateUser(userId, userDto)).thenReturn(updatedUserDto);

        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modoDto2)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"MEMBER"})
    public void testUpdateUser_AsMember_ShouldUpdateOwnProfile() throws Exception {
        // Given
        long userId = 1L; // This should match the authenticated user's ID

        // request runner   
        final UserPrincipal principal = UserPrincipal
            .builder()
            .email(userDto.getEmail())
            .authorities(userDto.getRoles().stream().map(r -> new SimpleGrantedAuthority(r)).toList())
            .userId(1L)
            .build();
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDto updatedUserDto = UserDto.builder().id(userId).username("updateUsername").build();

        when(userService.updateUser(userId, userDto)).thenReturn(updatedUserDto);

        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"MEMBER"})
    public void testUpdateUser_AsMember_ShouldNotUpdateOtherProfile() throws Exception {
        // Given
        long userId = 5L;

        // request runner
        final UserPrincipal principal = UserPrincipal
            .builder()
            .email(userDto.getEmail())
            .authorities(userDto.getRoles().stream().map(r -> new SimpleGrantedAuthority(r)).toList())
            .userId(1L)
            .build();
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDto updatedUserDto = UserDto.builder().id(userId).username("updateUsername").build();

        when(userService.updateUser(userId, userDto)).thenReturn(updatedUserDto);

        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto2)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeleteUser_Admin() throws Exception {
        long userId = 1L;

        when(userService.deleteUser(userId)).thenReturn(true);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "MODERATOR")
    void testDeleteUser_Moderator() throws Exception {
        long userId = 1L;

        when(userService.getUserRoleById(userId)).thenReturn(Set.of(Role.builder().name("MEMBER").build()));
        when(userService.deleteUser(userId)).thenReturn(true);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "MODERATOR")
    void testDeleteUser_Moderator_NotAuthorized() throws Exception {
        long userId = 3L;

        when(userService.getUserRoleById(userId)).thenReturn(Set.of(Role.builder().name("ADMIN").build()));

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetUserById() throws Exception {
        long userId = 1L;

        when(userService.getUserById(userId)).thenReturn(Optional.of(userDto));

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("userUser"));
    }

    @Test
    void testGetUsers() throws Exception {
        List<UserDto> users = List.of(userDto, userDto2);

        when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(users.size()));
    }

    @Test
    void testGetUsersByUsername() throws Exception {
        String username = "test";
        List<UserDto> users = List.of(userDto, userDto2);

        when(userService.getUsersByUsername(username)).thenReturn(users);

        mockMvc.perform(get("/api/users/search/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(users.size()));
    }

}
