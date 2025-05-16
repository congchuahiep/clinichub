package com.kh.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableTransactionManagement
@ComponentScan(basePackages = {
        "com.kh.controllers",
        "com.kh.services",
        "com.kh.repositories",
})
@PropertySource("classpath:cloudinary.properties")
public class SecurityConfigs {

    /**
     * Khởi tạo Bean bCryptPasswordEncoder
     */
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
                // Tắt cấu hình csrf (Cấu hình này ngăn chặn request từ domain khác)
                .csrf(c -> c.disable())

                // Cấu hình yêu cầu xác thực khi truy cập endpoints
                .authorizeHttpRequests(requests -> requests
                        // Yêu cầu xác thực khi truy cập vào `/` hoặc `/home`
                        .requestMatchers("/", "/home").authenticated()
                        // Cho phép tất cả mọi người truy cập vào endpoint này
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/api/**").permitAll())

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
}