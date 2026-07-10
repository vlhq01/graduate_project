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
                // TẮT CSRF TRIỆT ĐỂ
                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Tắt anonymous để tránh việc gán principal mặc định "anonymousUser"
                .anonymous(anonymous -> anonymous.disable())

                // BẮT ĐẦU ĐỊNH NGHĨA QUY TẮC
                .authorizeHttpRequests(auth -> auth
                        // Cho phép GET sản phẩm không cần đăng nhập
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // Cho phép POST vào /api/auth/sync không cần đăng nhập
                        .requestMatchers(HttpMethod.POST, "/api/auth/sync").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/products/homescreen",
                                "/api/products/categories"
                        ).permitAll()

                        // Mọi request khác đều phải xác thực
                        .anyRequest().authenticated()
                )

                // Trả về 401 Unauthorized thay vì 403 khi chưa đăng nhập
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )

                // KHÔNG DÙNG SESSION
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // THÊM BỘ LỌC CỦA CHÚNG TA VÀO
                .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Cấu hình này để cho phép App Android từ một "địa chỉ" khác gọi vào API của bạn
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
