package com.thanhdw.identify_service.configuration;

import com.thanhdw.identify_service.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity

/*
 * co 2 cach phan quyen
 * 1. Phan quyen tren endpoint nhu
 *   .requestMatchers(HttpMethod.GET, "/user")
 *   .hasAuthority("ROLE_ADMIN")
 * 2. Phan quyen theo method
 *
 * */
@EnableMethodSecurity
public class SecurityConfig {
    
    private final String[] PUBLIC_ENDPOINTS = {
            "/user", "/auth/token", "/auth/introspect", "auth/logout","auth/refresh",
            };
    
    private CustomerJwtDecoder customerJwtDecoder;
    //    @Value("${jwt.singing-key}")
    //    private String signingKey;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                /* 1) Kích hoạt CORS trước security filters */
                .cors(Customizer.withDefaults())
                
                /* 2) Tắt CSRF cho API stateless */
                .csrf(AbstractHttpConfigurer::disable)
                
                /* 3) Cho phép pre-flight OPTIONS + endpoint public */
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()   // <-- quan trọng
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                        //Xác định các endpoint cần xác thực
                        //                        .requestMatchers(HttpMethod.GET, "/user")
                        //                        .hasAuthority("ROLE_ADMIN") - cach 1
                        //                        .hasRole(Role.ADMIN.name())//cach 2
                        .anyRequest().authenticated())
                
                /* 4) Resource server JWT */
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt
                                .decoder(customerJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                
                .build();
    }
    
    //costomize jwtAuthenticationConverter
    //dùng để lấy các quyền từ token và chuyển đổi thành các quyền trong hệ thống
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
    //chiu trach nhiem cho viec verify token
    //    @Bean
    //    JwtDecoder jwtDecoder() {
    //        SecretKeySpec secretKeySpec = new SecretKeySpec(signingKey.getBytes(), "HmacSHA256");
    //        return NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512).build();
    //    }
    //init Bean PasswordEncoder
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
    @Bean
    public CorsFilter corsFilter() {
        log.info("Configuring CORS");
        CorsConfiguration config = new CorsConfiguration();
        //cho phep api truy cap tu origin nao
        // dung tu web naoo truy cap api do
        config.addAllowedOrigin("http://localhost:3000");
        //cho phep method nao
        config.addAllowedMethod("*");
        //cho phep header nao
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //khai bao theo tung endpoint
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration cfg = new CorsConfiguration();
//        cfg.setAllowedOriginPatterns(List.of("http://localhost:3000"));   // React dev-server
//        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
//        cfg.setAllowedHeaders(List.of("*"));           // có Authorization
//        cfg.setAllowCredentials(true);
//        cfg.setMaxAge(Duration.ofHours(1));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", cfg);
//        return source;
//    }
}