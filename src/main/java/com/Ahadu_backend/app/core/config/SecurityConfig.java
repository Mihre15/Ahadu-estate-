package com.Ahadu_backend.app.core.config;

import com.Ahadu_backend.app.auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.Ahadu_backend.app.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/listings",
                                "/register",
                                "/auth/register",
                                "/auth/login",
                                "/favicon.ico",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/agent/**",
                                "/api/listings/**",
                                "/buyer/**",
                                "/photo/**"
                        ).permitAll()
                        .requestMatchers("/agent/dashboard").hasAnyAuthority("AGENT", "ROLE_AGENT")
                        .requestMatchers("/buyer/me").hasAnyAuthority("BUYER", "ROLE_BUYER")
                        .requestMatchers("/agent/**").hasAnyAuthority("AGENT", "ROLE_AGENT")
                        .requestMatchers("/buyer/**").hasAnyAuthority("BUYER", "ROLE_BUYER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .build();
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .authorities(user.getRole().name())
                    .build();
        };
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}