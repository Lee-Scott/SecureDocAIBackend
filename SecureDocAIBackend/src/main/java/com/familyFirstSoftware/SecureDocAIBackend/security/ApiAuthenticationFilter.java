package com.familyFirstSoftware.SecureDocAIBackend.security;

import com.familyFirstSoftware.SecureDocAIBackend.domain.ApiAuthentication;
import com.familyFirstSoftware.SecureDocAIBackend.domain.Response;
import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.dtorequest.LoginRequest;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.LoginType;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.TokenType;
import com.familyFirstSoftware.SecureDocAIBackend.service.JwtService;
import com.familyFirstSoftware.SecureDocAIBackend.service.UserService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


import java.io.IOException;
import java.util.Map;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.LOGIN_PATH;
import static com.familyFirstSoftware.SecureDocAIBackend.utils.RequestUtils.getResponse;
import static com.familyFirstSoftware.SecureDocAIBackend.utils.RequestUtils.handleErrorResponse;
import static javax.swing.text.html.FormSubmitEvent.MethodType.POST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 1/12/2025
 *
 * Todo: rename not Api but with our app name like SecureDocAI
 */

@Slf4j
public class ApiAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final UserService userService;
    private final JwtService jwtService;


    public ApiAuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        super(new AntPathRequestMatcher(LOGIN_PATH, POST.name()), authenticationManager);
        this.userService = userService;
        this.jwtService = jwtService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
      try {
          var user = new ObjectMapper().configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true).readValue(request.getInputStream(), LoginRequest.class);
          userService.updateLoginAttempt(user.getEmail(), LoginType.LOGIN_ATTEMPT);
          var authentication = ApiAuthentication.unAuthenticated(user.getEmail(), user.getPassword());
          return getAuthenticationManager().authenticate(authentication);

      } catch (Exception exception) {
          log.error(exception.getMessage());
          handleErrorResponse(request, response, exception); // will give us a beautiful response
          return null;
      }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        var user =  (User)authResult.getPrincipal();
        userService.updateLoginAttempt(user.getEmail(), LoginType.LOGIN_SUCCESS);
        var httpResponse = user.isMfa() ? sendQrCode(request, user) : sendResponse(request, response, user);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(OK.value());
        var out = response.getOutputStream();
        var mapper = new ObjectMapper();
        mapper.writeValue(out, httpResponse);
        out.flush();

    }

    private Response sendResponse(HttpServletRequest request, HttpServletResponse response, User user) {
        jwtService.addCookie(response, user, TokenType.ACCESS);
        jwtService.addCookie(response, user, TokenType.REFRESH);
        return getResponse(request, Map.of("user", user), "Login successful.", OK);
    }

    private Object sendQrCode(HttpServletRequest request, User user) {
        return getResponse(request, Map.of("user", user), "Please scan the QR code below.", OK);
    }

}

