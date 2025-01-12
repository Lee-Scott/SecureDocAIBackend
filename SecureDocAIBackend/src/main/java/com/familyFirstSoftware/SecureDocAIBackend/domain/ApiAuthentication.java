package com.familyFirstSoftware.SecureDocAIBackend.domain;

import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 1/11/2025
 *
 * TODO: name should not be generic. Should be AISecureAuthentication or something
 */

public class ApiAuthentication extends AbstractAuthenticationToken {
    private static final String PASSWORD_PROTECTED = "[PASSWORD_PROTECTED]";
    private static final String EMAIL_PROTECTED = "[EMAIL_PROTECTED]";

    private User user;
    private String email;
    private String password;
    private boolean authenticated;

    // Call to start
    private ApiAuthentication(String email, String password) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.email = email;
        this.password = password;
        this.authenticated = false;
    }

    // call when we have a fully authenticated user
    // TODO: create my own authorities class and pass it in, Override GrantedAuthority
    private ApiAuthentication(User user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.user = user;
        this.email = EMAIL_PROTECTED;
        this.password = PASSWORD_PROTECTED;
        this.authenticated = true;
    }
    // Call to start
    public static ApiAuthentication unAuthenticated(String email, String password) {
        return new ApiAuthentication(email, password);
    }

    // call when we have a fully authenticated user
    // TODO: pass more
    public static ApiAuthentication authenticated(User user, Collection<? extends GrantedAuthority> authorities) {
        return new ApiAuthentication(user, authorities);
    }


    @Override
    public Object getCredentials() {
        return PASSWORD_PROTECTED;
    }

    // TODO: return something cool play around with it. could throw an exception if you dont want people to access it
    @Override
    public Object getPrincipal() {
        return this.user;
    }

    // We want them to go through our authentication process
    @Override
    public void setAuthenticated(boolean authenticated) {
       throw new ApiException("Cannot set authenticated");
    }

    @Override
    public boolean isAuthenticated() {
        return super.isAuthenticated();
    }

    // TODO: delete if not used
    public String getPassword(){
        return this.password;
    }

    public String getEmail(){
        return this.email;
    }
}

