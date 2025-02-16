package com.familyFirstSoftware.SecureDocAIBackend.security;

import com.familyFirstSoftware.SecureDocAIBackend.domain.ApiAuthentication;
import com.familyFirstSoftware.SecureDocAIBackend.domain.RequestContext;
import com.familyFirstSoftware.SecureDocAIBackend.domain.Token;
import com.familyFirstSoftware.SecureDocAIBackend.domain.TokenData;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.TokenType;
import com.familyFirstSoftware.SecureDocAIBackend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.*;
import static com.familyFirstSoftware.SecureDocAIBackend.enumeration.TokenType.ACCESS;
import static com.familyFirstSoftware.SecureDocAIBackend.enumeration.TokenType.REFRESH;
import static com.familyFirstSoftware.SecureDocAIBackend.utils.RequestUtils.handleErrorResponse;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 2/16/2025
 *
 * Todo: refactor this
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {


    private JwtService jwtService;

    /*
        This filter runs every time a request comes into the application. It runs once.
        If we can access the access token, and we set the authentication context using the access token then we can filterChain.doFilter(request, response);
         otherwise we can see if we can find the refresh token and create a new access token and set the authentication context using the new access token
         else we can clear the authentication context
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            var accessToken = jwtService.extractToken(request, ACCESS.getValue());
            if (accessToken.isPresent() && jwtService.getTokenData(accessToken.get(), TokenData::isValid)) { // present and valid
                SecurityContextHolder.getContext().setAuthentication(getAuthentication(accessToken.get(), request));
                RequestContext.setUserId(jwtService.getTokenData(accessToken.get(), TokenData::getUser).getId());
            } else {
                // Look for refresh token
                var refreshToken = jwtService.extractToken(request, REFRESH.getValue());
                if (accessToken.isPresent() && jwtService.getTokenData(accessToken.get(), TokenData::isValid)) {
                    var user = jwtService.getTokenData(refreshToken.get(), TokenData::getUser);
                    SecurityContextHolder.getContext().setAuthentication(getAuthentication(jwtService.createToken(user, Token::getAccess), request)); // create access token
                    jwtService.addCookie(response, user, TokenType.ACCESS);
                    RequestContext.setUserId(user.getId());
                } else { // we don't have access or refresh token
                    SecurityContextHolder.clearContext();
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            handleErrorResponse(request, response, exception);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        var shouldNotFilter = request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD) || Arrays.asList(PUBLIC_URLS).contains(request.getRequestURI());
        if(shouldNotFilter) { // if there is no user, it is the system
            RequestContext.setUserId(0L);
        }
        return shouldNotFilter;
    }

    private Authentication getAuthentication(String token, HttpServletRequest request) {
        var authentication = ApiAuthentication.authenticated(jwtService.getTokenData(token, TokenData::getUser), jwtService.getTokenData(token, TokenData::getAuthorities));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authentication;
    }
}