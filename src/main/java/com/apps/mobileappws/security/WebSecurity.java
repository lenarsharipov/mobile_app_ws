package com.apps.mobileappws.security;

import com.apps.mobileappws.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurity {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurity(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder
                = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder);

        // custom Authentication Manager
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // Customize Login URL path
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager);
        authenticationFilter.setFilterProcessesUrl("/users/login");

        http
                .cors().and()
                .csrf().disable().authorizeHttpRequests()
                .requestMatchers(POST, SecurityConstants.SIGN_UP_URL)
                .permitAll()
                .requestMatchers(GET, SecurityConstants.VERIFICATION_EMAIL_URL)
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .authenticationManager(authenticationManager) // security object must be updated with created custom Authentication Manager
                .addFilter(authenticationFilter) // registering created custom filter
                .addFilter(new AuthorizationFilter(authenticationManager))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Spring Security will never create
                // and use Http Session to obtain Security Context.
                // This means, because there is no Http Session created, for user authorization Spring Security will
                // rely only on info that is inside the jwt web-token.

        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("https://www.google.com"));
        configuration.setAllowedMethods(
                List.of(
                        GET.name(),
                        POST.name(),
                        PUT.name(),
                        DELETE.name()));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of(AUTHORIZATION, CACHE_CONTROL, CONTENT_TYPE));

        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
