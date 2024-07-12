package com.paulz.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paulz.user.entity.Role;
import com.paulz.user.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTests {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    public void RoleService_CreateRole_ReturnRole() {
        Role role = Role.builder().name("ADMIN").build();

        when(roleRepository.save(Mockito.any(Role.class))).thenReturn(role);
        Role savedRole = roleService.createRole("ADMIN");

        assertNotNull(savedRole);
    }

    @Test
    public void RoleService_GetRoles_ReturnRoles() {
        Role role1 = Role.builder().name("ADMIN").build();
        Role role2 = Role.builder().name("MEMBER").build();
        List<Role> roles = Arrays.asList(role1, role2);

        when(roleRepository.findAll()).thenReturn(roles);
        List<Role> savedRoles = roleService.getRoles();

        assertNotNull(savedRoles);
        assertTrue(savedRoles.size() > 0);
        assertTrue(savedRoles.contains(role1));
        assertTrue(savedRoles.contains(role2));
    }

    @Test
    public void RoleService_GetRoleByName_ReturnRole() {
        Role role = Role.builder().name("ADMIN").build();

        when(roleRepository.findByName(Mockito.any(String.class))).thenReturn(role);
        Role savedRole = roleService.getRoleByName("ADMIN");

        assertNotNull(savedRole);
        assertEquals(role.getName(), savedRole.getName());
    }
}






