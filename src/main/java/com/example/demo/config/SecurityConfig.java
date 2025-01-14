package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
            CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for secure password hashing
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                .requestMatchers( "/login", "/sign_up", "/css/**", "/js/**").permitAll()
                .requestMatchers("/social/posts/**","/social","/planning").permitAll()
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/admin/home").hasAuthority("ADMIN")
                .requestMatchers("/api/v1/recommendations/toggleRecActive/**").hasAuthority("ADMIN")
                .requestMatchers("/profile").authenticated()
                .requestMatchers("/social/posts/your-posts").authenticated()
                .requestMatchers("/social/update/**").authenticated()
                .requestMatchers("/planning/**").authenticated()
                .requestMatchers("/api/v1/user/login","/api/v1/user/add").permitAll()
                .requestMatchers("/api/v1/reply/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/post/**").authenticated()
                .anyRequest().permitAll()
                )
                .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customAuthenticationSuccessHandler)
                .permitAll()
                )
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/home")
                .invalidateHttpSession(true) // Hủy session
                .deleteCookies("JSESSIONID")
                .permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

}
