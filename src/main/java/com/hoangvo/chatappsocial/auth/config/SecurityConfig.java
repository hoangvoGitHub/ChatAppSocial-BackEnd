package com.hoangvo.chatappsocial.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {


    /**
     * Expiration time in milliseconds.
     */
    static final Long EXPIRATION_TIME = 1000L * 60 * 60 * 24; //1 day
    static final Long EXPIRATION_TIME_1_MINUTE = 1000L * 60 * 60 * 24; //1 day

    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * An authentication provider is responsible for authenticating user credentials and providing an authenticated Authentication object.
     */
    private final AuthenticationProvider authenticationProvider;

    @Bean
    //Config the security filter chain
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        SecurityContextRepository repo = new MyCustomSecurityContextRepository();

        httpSecurity.csrf(AbstractHttpConfigurer::disable) // Disable csrf, which will not prevent unauthorized requests
                .authorizeHttpRequests(auth -> auth //Configures the authorization rules for incoming HTTP requests
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .requestMatchers("/api/v1/chat/search/**").permitAll()
                        .requestMatchers("/api/v1/auth/**")
                        .permitAll() //Request that match the matchers will be permitted without requiring authentication.
                        .anyRequest().authenticated() // Other requests that does not match should be authenticated
                )
//                .exceptionHandling()
                .sessionManagement(sessionConfigure -> sessionConfigure
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    private static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**"
            // other public endpoints of your API may be appended to this array
    };
}