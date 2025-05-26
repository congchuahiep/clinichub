package com.kh.configs;

import com.kh.filters.JwtFilter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableTransactionManagement
@EnableMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = {
        "com.kh.controllers",
        "com.kh.services",
        "com.kh.repositories",
        "com.kh.filters"
})
@PropertySource("classpath:cloudinary.properties")
public class SecurityConfigs {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }
    
    
    /**
     * Bean này chịu trách nhiệm định nghĩa chuỗi các bộ lọc bảo mật (security
     * filters) sẽ được áp dụng cho các yêu cầu HTTP đến ứng dụng
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Tắt cấu hình csrf (Cấu hình này ngăn chặn request từ domain khác)
                .csrf(c -> c.disable())

                // Cấu hình các tầng filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)  // Thêm JWT filter

                // Cấu hình yêu cầu xác thực khi truy cập endpoints
                .authorizeHttpRequests(requests -> requests
                        // Yêu cầu xác thực khi truy cập vào `/` hoặc `/home` và những api yêu cầu xác thực
                        .requestMatchers("/", "/home").authenticated()

                        .requestMatchers("/doctors").permitAll()
                        .requestMatchers("/doctors/**").permitAll()
                        .requestMatchers("/statistics/**").permitAll()
                        .requestMatchers("/api/secure/**").authenticated()
                        // Cho phép tất cả mọi người truy cập vào endpoint này
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                )

                // Cấu hình trang đăng nhập
                .formLogin(form -> form
                        .loginPage("/login") // Trang đăng nhập nằm ở endpoint `/login`
                        .loginProcessingUrl("/login") // Endpoint xử lý đăng nhập nằm ở `/login`, tự động gọi
                        // loadUserByUsername
                        .defaultSuccessUrl("/", true) // Điều hướng trang đăng nhập về `/` khi thành công
                        .failureUrl("/login?error=true").permitAll()) // Điều hướng trang đăng nhập khi thất bại

                // Cấu hình đăng xuất
                .logout(logout -> logout
                        .logoutSuccessUrl("/login").permitAll()); // Chuyển về `/login` sau khi đăng xuất


        return http.build(); // Dựng
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:3000/")); 
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}