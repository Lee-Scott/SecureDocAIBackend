package com.familyFirstSoftware.SecureDocAIBackend.security;

import com.familyFirstSoftware.SecureDocAIBackend.service.UserService;
import com.familyFirstSoftware.SecureDocAIBackend.domain.ApiAuthentication;
import com.familyFirstSoftware.SecureDocAIBackend.domain.UserPrincipal;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;


import static com.familyFirstSoftware.SecureDocAIBackend.domain.ApiAuthentication.authenticated;
import static java.time.LocalDateTime.now;


/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 2/13/2025
 *
 * Todo: shouldn't be APIAuthenticationProvider. Should be AISecureAuthenticationProvider or something
 */

@Component
@RequiredArgsConstructor
public class ApiAuthenticationProvider implements AuthenticationProvider {
    private final UserService userService;
    private final BCryptPasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var apiAuthentication = authenticationFunction.apply(authentication);
        var user = userService.getUserByEmail(apiAuthentication.getEmail());
        if(user != null) {
            var userCredential = userService.getUserCredentialById(user.getId());
            //if(userCredential.getUpdatedAt().minusDays(NINETY_DAYS).isAfter(now())) { throw new ApiException("Credentials are expired. Please reset your password"); }
            //if(!user.isCredentialsNonExpired()) { throw new ApiException("Credentials are expired. Please reset your password"); }
            var userPrincipal = new UserPrincipal(user, userCredential);
            validAccount.accept(userPrincipal);
            if(encoder.matches(apiAuthentication.getPassword(), userCredential.getPassword())) {
                return authenticated(user, userPrincipal.getAuthorities());
            } else throw new BadCredentialsException("Email and/or password incorrect. Please try again");
        } throw new ApiException("Unable to authenticate");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiAuthentication.class.isAssignableFrom(authentication);
    }

    private final Function<Authentication, ApiAuthentication> authenticationFunction = authentication -> (ApiAuthentication) authentication;

    private final Consumer<UserPrincipal> validAccount = userPrincipal -> {
        if(!userPrincipal.isAccountNonLocked()) { throw new LockedException("Your account is currently locked"); }
        if(!userPrincipal.isEnabled()) { throw new DisabledException("Your account is currently disabled"); }
        if(!userPrincipal.isCredentialsNonExpired()) { throw new CredentialsExpiredException("Your password has expired. Please update your password"); }
        if(!userPrincipal.isAccountNonExpired()) { throw new DisabledException("Your account has expired. Please contact administrator"); }
    };
}