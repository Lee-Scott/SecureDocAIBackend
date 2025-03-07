package com.familyFirstSoftware.SecureDocAIBackend.resource;

import com.familyFirstSoftware.SecureDocAIBackend.domain.Response;
import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.dtorequest.EmailRequest;
import com.familyFirstSoftware.SecureDocAIBackend.dtorequest.QrCodeRequest;
import com.familyFirstSoftware.SecureDocAIBackend.dtorequest.ResetPasswordRequest;
import com.familyFirstSoftware.SecureDocAIBackend.dtorequest.UserRequest;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.TokenType;
import com.familyFirstSoftware.SecureDocAIBackend.service.JwtService;
import com.familyFirstSoftware.SecureDocAIBackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;


import static com.familyFirstSoftware.SecureDocAIBackend.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/14/2024
 *
 * This class handles user-related HTTP requests such as registration, account verification, and MFA setup.
 *
 *
 */
@Slf4j
@RestController
@RequestMapping(path = {"/user"})
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping(path = {"/register"})
    public ResponseEntity<Response> saveUser(@RequestBody @Valid UserRequest user, HttpServletRequest request) {
        userService.createUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword()); // could return newly created user
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Account created. Check your email for verification.", HttpStatus.CREATED));
    }


    @GetMapping(path = {"/verify/account"})
    public ResponseEntity<Response> verifyAccount(@RequestParam("key") String key, HttpServletRequest request) {
        userService.verifyAccountKey(key);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account verified.", HttpStatus.OK));
    }

    @GetMapping(path = {"/profile"})
    public ResponseEntity<Response> profile(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        var user = userService.getUserByUserId(userPrincipal.getUserId());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "Profile retrieved.", HttpStatus.OK));
    }

    // @AuthenticationPrincipal User needs to be authenticated to access this route
    @PatchMapping (path = {"/mfa/setup"})
    public ResponseEntity<Response> setupMfa(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        log.info("Authenticated user: {}", userPrincipal);
        var user = userService.setUpMfa(userPrincipal.getId());
        return ResponseEntity.ok((getResponse(request, Map.of("user", user), "MFA setup.", HttpStatus.OK)));
    }
    @PatchMapping(path = {"/mfa/cancel"})
    public ResponseEntity<Response> cancelMfa(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        userService.cancelMfa(userPrincipal.getId());
        var user = userService.setUpMfa(userPrincipal.getId());
        return ResponseEntity.ok(getResponse(request, Map.of("user", user), "MFA cancelled.", HttpStatus.OK));
    }

    @PostMapping(path = {"/verify/qrcode"})
    public ResponseEntity<Response> verifyQrcode(@RequestBody QrCodeRequest qrCodeRequest, HttpServletRequest request, HttpServletResponse response) {
        var user = userService.verifyQrCode(qrCodeRequest.getUserId(), qrCodeRequest.getQrCode());
        jwtService.addCookie(response, user, TokenType.ACCESS);
        jwtService.addCookie(response, user, TokenType.REFRESH);
        return ResponseEntity.ok(getResponse(request, Map.of("user", user), "QR Code Verified.", HttpStatus.OK));
    }

    // Start - Reset password when not logged in
    @PostMapping(path = {"/resetpassword"})
    public ResponseEntity<Response> resetPassword(@RequestBody @Valid EmailRequest emailRequest, HttpServletRequest request) {
        userService.resetPassword(emailRequest.getEmail());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Email sent to reset password.", HttpStatus.OK));
    }

    @GetMapping(path = {"/verify/password"})
    public ResponseEntity<Response> verifyPassword(@RequestParam ("key") String key, HttpServletRequest request) {
        var user = userService.verifyPasswordKey(key);
        return ResponseEntity.ok().body(getResponse(request,    Map.of("user", user), "Enter new password", HttpStatus.OK));
    }

    @PostMapping(path = {"/resetpassword/reset"})
    public ResponseEntity<Response> doResetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest, HttpServletRequest request) {
        userService.updatePassword(resetPasswordRequest.getUserId(), resetPasswordRequest.getNewPassword(), resetPasswordRequest.getConfirmNewPassword());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Password reset success.", HttpStatus.OK));
    }
    // End - Reset password when not logged in

    private URI getUri() {
        return URI.create("");
    }
}

