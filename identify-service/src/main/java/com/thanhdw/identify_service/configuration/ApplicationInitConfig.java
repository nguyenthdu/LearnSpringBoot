package com.thanhdw.identify_service.configuration;

import com.thanhdw.identify_service.constant.PredefinedRole;
import com.thanhdw.identify_service.entity.Role;
import com.thanhdw.identify_service.entity.User;
import com.thanhdw.identify_service.repository.RoleRepository;
import com.thanhdw.identify_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    
    PasswordEncoder passwordEncoder;
    @NonFinal
    static final String ADMIN_USER_NAME = "admin";
    
    @NonFinal
    static final String ADMIN_PASSWORD = "admin";
    @Bean
    @ConditionalOnProperty(
            prefix = "spring", value = "datasource.driverClassName", havingValue = "com.mysql.cj.jdbc.Driver"
    )
    //Ý nghĩa của @ConditionalOnProperty là chỉ chạy bean này khi thuộc tính datasource.driverClassName có giá trị là com.mysql.cj.jdbc.Driver
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        log.info("Initializing application...");
        return args -> {
            // Initialize the application here
            // For example, you can load initial data or perform any setup tasks
            if(userRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) {
               roleRepository.save(Role.builder()
                                           .name(PredefinedRole.USER_ROLE)
                                           .description("user role")
                                           .build());
               Role adminRole = roleRepository.save(Role.builder()
                                           .name(PredefinedRole.ADMIN_ROLE)
                                           .description("admin role")
                                           .build());
                User adminUser = User.builder()
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(new HashSet<>(List.of(adminRole)))
                        .build();
                userRepository.save(adminUser);
                log.warn("admin user has been created with default password: admin, please change it");
            }
            log.info("Application initialized successfully.");
        };
    }
}
