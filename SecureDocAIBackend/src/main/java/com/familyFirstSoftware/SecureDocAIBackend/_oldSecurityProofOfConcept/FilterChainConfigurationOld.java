package com.familyFirstSoftware.SecureDocAIBackend._oldSecurityProofOfConcept;


import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 1/10/2025
 *
 * Just a proof of concept isn't used in the actual project,
 * a simple example showing how spring security flows.
 * The most basic example I could come up with.
 *
 * Showing how spring security works, defaults are in ( )
 * Filters (UsernamePasswordAuthenticationFilter) -> Authentication Manager (ProviderManager) -> Authentication Provider (DaoAuthenticationProvider) -> UserDetailsService (InMemoryUserDetailsManager)
 *
 *  By default, spring has /login and /logout open
 */


@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@AllArgsConstructor
public class FilterChainConfigurationOld {

    private final BCryptPasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.requestMatchers("/user/login").permitAll()
                                .anyRequest().authenticated())
                .build();


    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder) {
        var myOwnAuthenticationProvider = new MyOwnAuthenticationProviderOld(userDetailsService);
        return new ProviderManager(myOwnAuthenticationProvider);
    }

    // Overwriting the default UserDetails implementation
    /*@Bean
    public UserDetailsService userDetailsService() {
        var lee = User.withDefaultPasswordEncoder()
                .username("lee")
                .password("{nope}letmein")
                .roles("USER")
                .build();
        var jim = User.withUsername("jim")
                .password(passwordEncoder.encode("letmein"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(List.of(lee, jim));
    }*/

    // another way to do it
    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        return new InMemoryUserDetailsManager(
                User.withUsername("lee")
                        .password("letmein")
                        .roles("USER")
                        .build(),
                User.withUsername("jim")
                        .password("letmein")
                        .roles("USER")
                        .build());

    }


}


