package com.splitmate.api.config;

import com.splitmate.api.entity.User;
import com.splitmate.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    public CommandLineRunner seedTestUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String testEmail = "murugan@example.com";

            if (userRepository.findByEmail(testEmail).isEmpty()) {
                User user = new User();
                user.setDisplayName("Murugan");
                user.setEmail(testEmail);
                user.setPhone("+919876543210");
                user.setPasswordHash(passwordEncoder.encode("Password@123"));
                user.setActive(true);

                userRepository.save(user);
                log.info("Test user seeded: {} / Password@123", testEmail);
            } else {
                log.info("Test user already exists: {}", testEmail);
            }
        };
    }
}
