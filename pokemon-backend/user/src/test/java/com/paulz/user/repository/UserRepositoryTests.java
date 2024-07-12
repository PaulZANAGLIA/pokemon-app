package com.paulz.user.repository;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.paulz.user.entity.User;


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void UserRepository_SaveUser_ReturnSavedUser(){
        // Arrange
        User user = new User();
        user.setUsername("John Doe");
        user.setEmail("john.doe@example.com");
        user.setElo(0);
        user.setPassword("mygreatpassword");

        // Act
        User foundUser = userRepository.save(user);

        // Assert
        assertNotNull(foundUser);
        assertEquals("John Doe", foundUser.getUsername());
    }

    @Test
    public void UserRepository_FindAllUsers_ReturnMoreThanOneUser(){
        // Arrange
        User user = new User();
        user.setUsername("John Doe");
        user.setEmail("john.doe@example.com");
        user.setElo(0);
        user.setPassword("mygreatpassword");
        
        User user2 = new User();
        user2.setUsername("Jane Doe");
        user2.setEmail("jane.doe@example.com");
        user2.setElo(0);
        user2.setPassword("mygreatpassword");

        // Act
        userRepository.save(user);
        userRepository.save(user2);
        List<User> foundUsers = userRepository.findAll();

        // Assert
        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size());
    }

    @Test
    public void UserRepository_FindUserById_ReturnFoundUser(){
        // Arrange
        User user = new User();
        user.setUsername("John Doe");
        user.setEmail("john.doe@example.com");
        user.setElo(0);
        user.setPassword("mygreatpassword");

        // Act
        userRepository.save(user);
        List<User> foundUsers = userRepository.findAll();

        // Assert
        assertNotNull(foundUsers);
        assertEquals(1, foundUsers.size());
    }

    @Test
    public void UserRepository_FindUserByEmail_ReturnFoundUser(){
        // Arrange
        User user = new User();
        user.setUsername("John Doe");
        user.setEmail("john.doe@example.com");
        user.setElo(0);
        user.setPassword("mygreatpassword");

        // Act
        userRepository.save(user);
        User foundUser = userRepository.findByEmail("john.doe@example.com").orElse(null);
        
         // Assert
         assertNotNull(foundUser);
         assertEquals("john.doe@example.com", foundUser.getEmail());
    }

    @Test
    public void UserRepository_FindUserByUsername_ReturnFoundUser(){
        // Arrange
        User user = new User();
        user.setUsername("Doe");
        user.setEmail("john.doe@example.com");
        user.setElo(0);
        user.setPassword("mygreatpassword");

        User user2 = new User();
        user2.setUsername("Doe");
        user2.setEmail("jane.doe@example.com");
        user2.setElo(0);
        user2.setPassword("mygreatpassword");
        
        // Act
        userRepository.save(user);
        userRepository.save(user2);
        List<User> foundUsers = userRepository.findByUsername("Doe");
        
         // Assert
         assertNotNull(foundUsers);
         assertEquals(2, foundUsers.size());
    }

    @Test
    public void UserRepository_DeleteUserById_ReturnNoUser(){
        // Arrange
        User user = new User();
        user.setUsername("John Doe");
        user.setEmail("john.doe@example.com");
        user.setElo(0);
        user.setPassword("mygreatpassword");

        // Act
        userRepository.save(user);
        userRepository.deleteById(user.getId());
        Optional<User> foundUser = userRepository.findById(1L);
        
        // Assert
        assertTrue(foundUser.isEmpty());
    }
}
