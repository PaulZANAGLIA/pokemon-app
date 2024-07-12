package com.paulz.user;

import java.util.Arrays;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.paulz.user.entity.Role;
import com.paulz.user.entity.User;
import com.paulz.user.repository.RoleRepository;
import com.paulz.user.repository.UserRepository;

@SpringBootApplication
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	@Bean
    @Profile("!test")
    CommandLineRunner init(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Define default roles
            if (roleRepository.count() == 0) {
                Role adminRole = new Role();
                adminRole.setName("ADMIN");

                Role memberRole = new Role();
                memberRole.setName("MEMBER");

                Role moderatorRole = new Role();
                moderatorRole.setName("MODERATOR");

                roleRepository.saveAll(Arrays.asList(adminRole, memberRole, moderatorRole));
            }

            // Define root user
            if (!userRepository.findByEmail("paulzanaglia@gmail.com").isPresent()) {
                User rootUser = new User();
                rootUser.setEmail("paulzanaglia06@gmail.com");
                rootUser.setUsername("root");
                rootUser.setPassword(passwordEncoder.encode("root"));
                rootUser.setElo(0);
                rootUser.setRoles(Set.of(roleRepository.findByName("ADMIN")));
                rootUser.setFriends(Set.of());
                userRepository.save(rootUser);
            }
        };
    }
}
