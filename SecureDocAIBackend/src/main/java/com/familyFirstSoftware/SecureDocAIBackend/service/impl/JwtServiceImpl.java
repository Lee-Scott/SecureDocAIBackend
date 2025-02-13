package com.familyFirstSoftware.SecureDocAIBackend.service.impl;

import com.familyFirstSoftware.SecureDocAIBackend.domain.Token;
import com.familyFirstSoftware.SecureDocAIBackend.domain.TokenData;
import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.TokenType;
import com.familyFirstSoftware.SecureDocAIBackend.function.TriConsumer;
import com.familyFirstSoftware.SecureDocAIBackend.security.JwtConfiguration;
import com.familyFirstSoftware.SecureDocAIBackend.service.JwtService;
import com.familyFirstSoftware.SecureDocAIBackend.service.UserService;
import io.jsonwebtoken.Claims;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.*;

import static com.familyFirstSoftware.SecureDocAIBackend.enumeration.TokenType.ACCESS;
import static com.familyFirstSoftware.SecureDocAIBackend.enumeration.TokenType.REFRESH;
import static java.util.Arrays.stream;


import static java.util.Optional.empty;
import static org.springframework.boot.web.server.Cookie.SameSite.NONE;
import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 2/3/2025
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl extends JwtConfiguration implements JwtService {

    private final UserService userService;

    private final Supplier<SecretKey> key = () -> Keys.hmacShaKeyFor(Decoders.BASE64.decode(getSecret()));

    private final Function<String, Claims> claimsFunction = token ->
            Jwts.parser()
                    .verifyWith(key.get())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

    //                     <token, userId>
    private final Function<String, String> subject = token -> getClaimsValue(token, Claims::getSubject);

    /*  This method extracts the token from the request

        TODO: clean this mess up. Extract the null pointer logic out.

        Returns an Optional .of whatever the result is or empty.
        We want to see if we have any cookies, so we don't throw a null pointer exception.
        If getCookies returns null creates a fake empty cookie, so we have something to work with.
        Otherwise, call getCookies.
        We then filter all cookies where the name is equal and grab the value.

     */
    private final BiFunction<HttpServletRequest, String, Optional<String>> extractToken = (request, cookieName) ->
            Optional.of(stream(request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)} : request.getCookies())
                            .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                            .map(Cookie::getValue)
                            .findAny())
                    .orElse(empty());

    //                  Takes request and cookie name and returns Optional<Cookies>
    private final BiFunction<HttpServletRequest, String, Optional<Cookie>> extractCookie = (request, cookieName) ->
            Optional.of(stream(request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)} : request.getCookies())
                    .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                    .findAny()).orElse(empty());

    private final Supplier<JwtBuilder> builder = () ->
            Jwts.builder()
                    .header().add(Map.of(TYPE, JWT_TYPE))
                    .and()
                    .audience().add(FAMILY_FIRST_SOFTWARE)
                    .and()
                    .id(UUID.randomUUID().toString()) // TODO: build custom logic around this token. Save it in memory and when a request comes in you can check for it.
                    .issuedAt(Date.from(Instant.now()))
                    .notBefore(Date.from(Instant.now()))
                    .signWith(key.get(), Jwts.SIG.HS512); // can call key.get().getAlgorithm() to see what the algorithm is

    private final BiFunction<User, TokenType, String> buildToken = (user, type) ->
            Objects.equals(type, ACCESS) ? builder.get()
                    .subject(user.getId().toString())
                    .claim(AUTHORITIES, user.getAuthorities())
                    .claim(ROLE, user.getRole())
                    .expiration(Date.from(Instant.now().plusSeconds(getExpiration())))
                    .compact() : builder.get() // if it's an access token compact, or it's a refresh token, so we get the builder again
                    .subject(String.valueOf(user.getUserId()))
                    .expiration(Date.from(Instant.now().plusSeconds(getExpiration()))) // you can have a different expiration for refresh token
                    .compact();




    private final TriConsumer<HttpServletResponse, User, TokenType> addCookie = ((response, user, type) -> {
        switch (type) {
            case ACCESS -> {
                var accessToken = createToken(user, Token::getAccess);
                var cookie = new Cookie(type.getValue(), accessToken); // value and name
                cookie.setHttpOnly(true); // can't access the cookie from js
                //cookie.setSecure(true);
                cookie.setMaxAge(2 * 60);
                cookie.setPath("/"); // if we set this to /user/login it will only work for that route. this always sends it from the domain it came from
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
            case REFRESH -> {
                var refreshToken = createToken(user, Token::getAccess);
                var cookie = new Cookie(type.getValue(), refreshToken); // value and name
                cookie.setHttpOnly(true); // can't access the cookie from js
                //cookie.setSecure(true);
                cookie.setMaxAge(2 * 60 * 60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);

            }
        }
    });

    private <T> T getClaimsValue(String token, Function<Claims, T> claims) {
        return claimsFunction.andThen(claims).apply(token);

    }

    // Takes <token> returns authorities separated by commas
    public Function<String, List<GrantedAuthority>> authorities = token ->
            commaSeparatedStringToAuthorityList(new StringJoiner(AUTHORITY_DELIMITER)
                    .add(claimsFunction.apply(token).get(AUTHORITIES, String.class))
                    .add(ROLE_PREFIX + claimsFunction.apply(token).get(ROLE, String.class)).toString());




    @Override
    public String createToken(User user, Function<Token, String> tokenFunction) {
        var token = Token.builder().access(buildToken.apply(user, ACCESS)).refresh(buildToken.apply(user, REFRESH)).build();
        return tokenFunction.apply(token);
    }

    @Override
    public Optional<String> extractToken(HttpServletRequest request, String cookieName) {
        return extractToken(request, cookieName);

    }

    @Override
    public void addCookie(HttpServletResponse response, User user, TokenType type) {
        addCookie.accept(response, user, type);

    }

    // Generic is everything that exists on the TokenData
    @Override
    public <T> T getTokenData(String token, Function<TokenData, T> tokenFunction) {
        return tokenFunction.apply(TokenData.builder()
                .valid(Objects.equals(userService.getUserByUserId(subject.apply(token)).getUserId(), claimsFunction.apply(token).getSubject()))
                .authorities(authorities.apply(token))
                .claims(claimsFunction.apply(token))
                .user(userService.getUserByUserId(subject.apply(token)))
                .build());
    }

    @Override
    public void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        var optionalCookie = extractCookie.apply(request, cookieName);
        if(optionalCookie.isPresent()) {
            var cookie = optionalCookie.get();
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}

