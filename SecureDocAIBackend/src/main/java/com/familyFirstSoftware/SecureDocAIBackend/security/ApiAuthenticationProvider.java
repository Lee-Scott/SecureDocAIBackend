package com.familyFirstSoftware.SecureDocAIBackend.security;

import com.familyFirstSoftware.SecureDocAIBackend.domain.ApiAuthentication;
import com.familyFirstSoftware.SecureDocAIBackend.domain.UserPrincipal;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.familyFirstSoftware.SecureDocAIBackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.NINETY_DAYS;

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

    // Authenticate the user using the authentication request and userDetailsService
    // You can do anything here, call the db, etc.
    // Todo: fit the if(user != null) and var user logic with try catch
    // Todo: refactor for SOLD principals
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
       var apiAuthentication = authenticationFunction.apply(authentication);
       var user = userService.getUserByEmail(apiAuthentication.getEmail());
       if(user != null){ // shouldn't be possible, or it would break at var user = ...
           var userCredential = userService.getUserCredentialById(user.getUserId());
           if(user.isCreditNonExpired()){throw new ApiException("Credential are expired. Please reset your password.");}
            var userPrincipal = new UserPrincipal(user, userCredential);
            validAccount.accept(userPrincipal);
            if(encoder.matches(apiAuthentication.getPassword(), userPrincipal.getPassword())){
                return ApiAuthentication.authenticated(user, userPrincipal.getAuthorities());
            } else throw new BadCredentialsException("Email and/or password is incorrect. Please try again.");
       } throw new ApiException("Unable to authenticate.");
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return ApiAuthentication.class.isAssignableFrom(authentication);
    }

    // just for casting
    private final Function<Authentication, ApiAuthentication> authenticationFunction = authentication -> (ApiAuthentication) authentication;

    private final Consumer<UserPrincipal> validAccount = userPrincipal -> {
        if (userPrincipal.isAccountNonLocked()) {throw new LockedException("Your account is locked.");}
        if (userPrincipal.isEnabled()) {throw new DisabledException("Your account is disabled.");}
        if (userPrincipal.isCredentialsNonExpired()) {throw new CredentialsExpiredException("Your password is expired. Please reset your password.");}
        if (userPrincipal.isAccountNonExpired()) {throw new DisabledException("Your account is expired. Please contact admin.");}
    };
}