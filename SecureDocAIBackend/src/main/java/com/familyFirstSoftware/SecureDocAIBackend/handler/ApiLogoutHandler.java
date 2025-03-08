package com.familyFirstSoftware.SecureDocAIBackend.handler;

import com.familyFirstSoftware.SecureDocAIBackend.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import static com.familyFirstSoftware.SecureDocAIBackend.enumeration.TokenType.ACCESS;
import static com.familyFirstSoftware.SecureDocAIBackend.enumeration.TokenType.REFRESH;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 3/8/2025
 */

@RequiredArgsConstructor
@Service
public class ApiLogoutHandler implements LogoutHandler {
    private final JwtService jwtService; // needed to remove the cookie from the browser

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        var logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);
        jwtService.removeCookie(request, response, ACCESS.getValue());
        jwtService.removeCookie(request, response, REFRESH.getValue());

    }
}

