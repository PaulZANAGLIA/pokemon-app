package com.paulz.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.paulz.user.entity.Role;


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void RoleRepository_testSave() {
        Role adminRole = Role.builder().name("ADMIN").build();

        Role savedRole = roleRepository.save(adminRole);

        assertNotNull(savedRole);
        assertEquals("ADMIN", savedRole.getName());
    }

    @Test
    public void RoleRepository_SaveAllRoles_ReturnSavedRoles() {
        Role adminRole = Role.builder().name("ADMIN").build();
        Role modoRole = Role.builder().name("MODERATOR").build();
        Role memberRole = Role.builder().name("MEMBER").build();

        List<Role> savedRoles = roleRepository.saveAll(List.of(adminRole, modoRole, memberRole));

        assertNotNull(savedRoles);
        assertEquals(3, savedRoles.size());
        assertEquals("ADMIN", savedRoles.get(0).getName());
        assertEquals("MODERATOR", savedRoles.get(1).getName());
        assertEquals("MEMBER", savedRoles.get(2).getName());
    }

    @Test
    public void RoleRepository_CountRoles_ReturnExpectedNumber() {
        Role adminRole = Role.builder().name("ADMIN").build();
        Role modoRole = Role.builder().name("MODERATOR").build();
        Role memberRole = Role.builder().name("MEMBER").build();

        roleRepository.saveAll(List.of(adminRole, modoRole, memberRole));

        long count = roleRepository.count();

        assertEquals(3L, count);
    }

    @Test
    public void RoleRepository_testFindByName() {
        Role role = Role.builder().name("ADMIN").build();

        roleRepository.save(role);
        Role foundRole = roleRepository.findByName("ADMIN");

        assertNotNull(foundRole);
        assertEquals("ADMIN", foundRole.getName());
    }

    @Test
    public void RoleRepository_testFindAll() {
        Role adminRole = Role.builder().name("ADMIN").build();
        Role modoRole = Role.builder().name("MODERATOR").build();
        Role memberRole = Role.builder().name("MEMBER").build();

        roleRepository.saveAll(List.of(adminRole, modoRole, memberRole));
        List<Role> foundRoles = roleRepository.findAll();

        assertNotNull(foundRoles);
        assertEquals(3, foundRoles.size());
        assertEquals("ADMIN", foundRoles.get(0).getName());
        assertEquals("MODERATOR", foundRoles.get(1).getName());
        assertEquals("MEMBER", foundRoles.get(2).getName());
    }
}
