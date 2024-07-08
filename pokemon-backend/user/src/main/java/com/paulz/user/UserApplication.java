package com.paulz.user;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.paulz.user.model.Role;
import com.paulz.user.model.User;
import com.paulz.user.repository.RoleRepository;
import com.paulz.user.repository.UserRepository;

@SpringBootApplication
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	@Bean
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
                rootUser.setUsername("root");
                rootUser.setEmail("paulzanaglia06@gmail.com");
                rootUser.setPassword(passwordEncoder.encode("root"));

                HashSet<Role> roles = new HashSet<>();
                Role adminRole = roleRepository.findByName("ADMIN");
                roles.add(adminRole);
                rootUser.setRoles(roles);

                userRepository.save(rootUser);
            }
        };
    }
}
