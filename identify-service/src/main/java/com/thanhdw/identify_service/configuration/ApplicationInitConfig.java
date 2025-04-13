package com.thanhdw.identify_service.configuration;

import com.thanhdw.identify_service.entity.User;
import com.thanhdw.identify_service.enums.Role;
import com.thanhdw.identify_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE ,makeFinal = true)
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
            // Initialize the application here
            // For example, you can load initial data or perform any setup tasks
            if(userRepository.findByUsername("admin").isEmpty()) {
                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.name());
                User user = User.builder().username("admin").password(passwordEncoder.encode("admin")).roles(roles).build();
                userRepository.save(user);
                log.warn("Admin user created with username: {}", user.getUsername(), "password: {}", "admin please change password");
            }
            System.out.println("Application initialized successfully.");
        };
    }
}
