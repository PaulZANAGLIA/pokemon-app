package com.paulz.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paulz.user.entity.Role;
import com.paulz.user.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoleControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    private Role role;
    private Role role2;

    @BeforeEach
    public void init() {
        role = Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .build();

        role2 = Role.builder()
                .id(2L)
                .name("ROLE_ADMIN")
                .build();
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    public void testCreateRole_ShouldCreateRole() throws Exception {
        String roleName = "ROLE_USER";
        when(roleService.createRole(roleName)).thenReturn(role);
        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleName)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"MODERATOR"})
    public void testCreateRole_AsModo_ShouldNotCreateRole() throws Exception {
        String roleName = "ROLE_USER";
        when(roleService.createRole(roleName)).thenReturn(role);
        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleName)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"MEMBER"})
    public void testGetRoles_AsMember_ShouldReturnRolesList() throws Exception {
        List<Role> roles = List.of(role, role2);
        when(roleService.getRoles()).thenReturn(roles);

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(roles.size()))
                .andExpect(jsonPath("$[0].name").value("ROLE_USER"))
                .andExpect(jsonPath("$[1].name").value("ROLE_ADMIN"));
    }
}
