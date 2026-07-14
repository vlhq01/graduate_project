package com.example.techadvisor.config;

import com.example.techadvisor.security.FirebaseTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration // Báo cho Spring biết đây là file cấu hình
@EnableWebSecurity // Bật tính năng bảo mật web
public class SecurityConfig {

    @Autowired
    private FirebaseTokenFilter firebaseTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .anonymous(anonymous -> anonymous.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/auth/sync").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/products/homescreen",
                                "/api/products/categories"
                        ).permitAll()

                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//
                .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Cho phép từ mọi nguồn
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
