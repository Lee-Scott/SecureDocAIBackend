package com.familyFirstSoftware.SecureDocAIBackend._securityProofOfConcept;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 1/11/2025
 *
 * Just a proof of concept isn't used in the actual project
 */

@Component
@RequiredArgsConstructor
public class MyOwnAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    // Authenticate the user using the authentication request and userDetailsService
    // You can do anything here, call the db, etc.
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var userToAuthenticate = (UsernamePasswordAuthenticationToken) authentication;
        var userFromDb = userDetailsService.loadUserByUsername((String) userToAuthenticate.getPrincipal());
        var password = (String) userToAuthenticate.getCredentials();
        if(password.equals(userFromDb.getPassword())) {
            // they are authenticated!
            return UsernamePasswordAuthenticationToken.authenticated(userFromDb, "{PASSWORD PROTECTED}", userFromDb.getAuthorities());
        }

        throw new BadCredentialsException("Unable to login");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

