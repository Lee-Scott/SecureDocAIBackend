package com.familyFirstSoftware.SecureDocAIBackend.domain;

import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.entity.CredentialEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 2/13/2025
 */

@Slf4j
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    @Getter
    private final User user;
    private final CredentialEntity credentialEntity;

    /*@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(user.getAuthorities());
    }*/

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Get the individual authorities from the comma-separated string
        Collection<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(user.getAuthorities());

        // Add the role as an authority with ROLE_ prefix
        if (user.getRole() != null && !user.getRole().isEmpty()) {
            List<GrantedAuthority> updatedAuthorities = new ArrayList<>(authorities);
            updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
            return updatedAuthorities;
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        if (credentialEntity == null) {
            log.error("User credentials are null for user: {}", user.getEmail());
            return null;
        }
        return credentialEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}

