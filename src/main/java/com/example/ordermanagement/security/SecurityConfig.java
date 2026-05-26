package com.example.ordermanagement.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_MANAGER = "MANAGER";
    private static final String ROLE_CUSTOMER = "CUSTOMER";

    private static final String PRODUCTS_API = "/api/v1/products/**";
    private static final String INVENTORY_API = "/api/v1/inventory/**";
    private static final String ORDERS_API = "/api/v1/orders/**";
    private static final String PAYMENTS_API = "/api/v1/payments/**";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, PRODUCTS_API)
                        .hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_CUSTOMER)

                        .requestMatchers(HttpMethod.POST, PRODUCTS_API)
                        .hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)

                        .requestMatchers(HttpMethod.PUT, PRODUCTS_API)
                        .hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)

                        .requestMatchers(HttpMethod.DELETE, PRODUCTS_API)
                        .hasRole(ROLE_ADMIN)

                        .requestMatchers(INVENTORY_API)
                        .hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)

                        .requestMatchers(ORDERS_API)
                        .hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_CUSTOMER)

                        .requestMatchers(PAYMENTS_API)
                        .hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)

                        .anyRequest()
                        .authenticated()
                )

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}