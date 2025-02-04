package com.familyFirstSoftware.SecureDocAIBackend.service;

import com.familyFirstSoftware.SecureDocAIBackend.domain.Token;
import com.familyFirstSoftware.SecureDocAIBackend.domain.TokenData;
import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 1/30/2025
 *
 * Json Web Token Service
 */

public interface JwtService {
    String createToken(User user, Function<Token, String> tokenFunction);
    Optional<String> extractToken(HttpServletRequest request, String tokenType);

    // in the past I have put the token in the header, but this is a much more secure approach
    void addCookie(HttpServletResponse response, User user, TokenType token);
    <T> T getTokenData(String token, Function<TokenData, T> tokenFunction);
    void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName);
}
