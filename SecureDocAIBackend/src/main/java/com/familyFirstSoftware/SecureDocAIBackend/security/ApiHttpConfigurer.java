package com.familyFirstSoftware.SecureDocAIBackend.security;

import com.familyFirstSoftware.SecureDocAIBackend.service.JwtService;
import com.familyFirstSoftware.SecureDocAIBackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 2/18/2025
 *
 *
 */

@Component
@RequiredArgsConstructor
public class ApiHttpConfigurer extends AbstractHttpConfigurer<ApiHttpConfigurer, HttpSecurity> {
    private final AuthorizationFilter authorizationFilter;
    private final ApiAuthenticationProvider apiAuthenticationProvider;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Override
    public void init(HttpSecurity http) throws Exception {
        http.authenticationProvider(apiAuthenticationProvider);

    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(new ApiAuthenticationFilter(authenticationConfiguration.getAuthenticationManager(), userService, jwtService), UsernamePasswordAuthenticationFilter.class);
    }
}

