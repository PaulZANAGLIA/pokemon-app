package com.paulz.user.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.paulz.user.dto.UserDto;
import com.paulz.user.security.UserPrincipal;
import com.paulz.user.service.UserService;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FriendControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserDto userDto;
    private UserDto userDto2;

    @BeforeEach
    public void init() {
        userDto = UserDto.builder()
                .id(1L)
                .username("userUser")
                .email("user@example.com")
                .roles(Set.of("MEMBER"))
                .friends(Set.of())
                .elo(0)
                .build();

        userDto2 = UserDto.builder()
                .id(2L)
                .username("user2User")
                .email("user2@example.com")
                .roles(Set.of("MEMBER"))
                .friends(Set.of())
                .elo(1)
                .build();
    }

    @Test
    @WithMockUser(authorities = {"MEMBER"}, username = "userUser")
    public void testGetFriends_ShouldReturnFriendsList() throws Exception {
        long userId = 1L;
        List<UserDto> friends = List.of(userDto2);

        when(userService.getFriends(userId)).thenReturn(friends);

        mockMvc.perform(get("/api/friends/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(friends.size()))
                .andExpect(jsonPath("$[0].username").value("user2User"));
    }

    @Test
    @WithMockUser(authorities = {"MEMBER"})
    public void testAddFriend_ShouldAddFriend() throws Exception {
        long userId = 1L;
        long friendId = 2L;

        // request runner   
        final UserPrincipal principal = UserPrincipal
            .builder()
            .email(userDto.getEmail())
            .authorities(userDto.getRoles().stream().map(r -> new SimpleGrantedAuthority(r)).toList())
            .userId(1L)
            .build();
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        

        when(userService.getFriends(userId)).thenReturn(List.of(userDto2));
        mockMvc.perform(post("/api/friends/{userId}/{friendId}", userId, friendId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("user2User"));
    }

    @Test
    @WithMockUser(username = "userUser", authorities = {"MEMBER"})
    public void testRemoveFriend_ShouldRemoveFriend() throws Exception {
        long userId = 1L;
        long friendId = 2L;

        // request runner   
        final UserPrincipal principal = UserPrincipal
            .builder()
            .email(userDto.getEmail())
            .authorities(userDto.getRoles().stream().map(r -> new SimpleGrantedAuthority(r)).toList())
            .userId(1L)
            .build();
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        

        doNothing().when(userService).removeFriend(userId, friendId);

        mockMvc.perform(delete("/api/friends/{userId}/{friendId}", userId, friendId))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend removed successfully!"));
    }

    @Test
    @WithMockUser(username = "otherUser", authorities = {"MEMBER"})
    public void testRemoveFriend_ShouldNotRemoveFriend_WhenUserIdDoesNotMatch() throws Exception {
        long userId = 1L;
        long friendId = 2L;


        // request runner   
        final UserPrincipal principal = UserPrincipal
            .builder()
            .email(userDto.getEmail())
            .authorities(userDto.getRoles().stream().map(r -> new SimpleGrantedAuthority(r)).toList())
            .userId(userId)
            .build();
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        

        mockMvc.perform(delete("/api/friends/{userId}/{friendId}", friendId, 999L))
                .andExpect(status().isForbidden());
    }
}