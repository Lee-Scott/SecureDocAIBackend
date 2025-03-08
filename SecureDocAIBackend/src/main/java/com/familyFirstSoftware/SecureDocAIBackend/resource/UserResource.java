package com.familyFirstSoftware.SecureDocAIBackend.resource;

import com.familyFirstSoftware.SecureDocAIBackend.domain.Response;
import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.dtorequest.*;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.TokenType;
import com.familyFirstSoftware.SecureDocAIBackend.handler.ApiLogoutHandler;
import com.familyFirstSoftware.SecureDocAIBackend.service.JwtService;
import com.familyFirstSoftware.SecureDocAIBackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;


import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.PHOTO_DIRECTORY;
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
    private final ApiLogoutHandler apiLogoutHandler;

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

    // User ID comes from userPrincipal, but the rest of the information needed to update comes from userRequest.
    @PatchMapping(path = {"/update"})
    public ResponseEntity<Response> update(@AuthenticationPrincipal User userPrincipal, @RequestBody UserRequest userRequest, HttpServletRequest request) {
        var user = userService.updateUser(userPrincipal.getUserId(), userRequest.getFirstName(), userRequest.getLastName(), userRequest.getEmail(), userRequest.getPhone(), userRequest.getBio());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "User updated successfully.", HttpStatus.OK));
    }

    @PatchMapping(path = {"/updateRole"})
    public ResponseEntity<Response> updateRole(@AuthenticationPrincipal User userPrincipal, @RequestBody RoleRequest roleRequest, HttpServletRequest request) {
        userService.updateRole( userPrincipal.getUserId(), roleRequest.getRole());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Role updated successfully.", HttpStatus.OK));
    }

    @PatchMapping(path = {"/toggleAccountExpired"})
    public ResponseEntity<Response> toggleAccountExpired(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.toggleAccountExpired(user.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Toggle account expired successfully.", HttpStatus.OK));
    }

    @PatchMapping(path = {"/toggleAccountLocked"})
    public ResponseEntity<Response> toggleAccountLocked(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.toggleAccountLocked(user.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Toggle account locked successfully.", HttpStatus.OK));
    }
    @PatchMapping(path = {"/toggleAccountEnabled"})
    public ResponseEntity<Response> toggleAccountEnabled(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.toggleAccountEnabled(user.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Toggle account enabled successfully.", HttpStatus.OK));
    }
    @PatchMapping(path = {"/toggleCredentialsExpired"})
    public ResponseEntity<Response> toggleCredentialsExpired(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.toggleCredentialsExpired(user.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Toggle credentials expired successfully.", HttpStatus.OK));
    }
    @PatchMapping(path = {"/makeCredentialsExpired"})
    public ResponseEntity<Response> makeCredentialsExpired(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.makeCredentialsExpired(user.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Make credentials expired successfully.", HttpStatus.OK));
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

    // Start - Reset password when user IS logged in
    @PatchMapping(path = {"/updatePassword"})
    public ResponseEntity<Response> updatePassword(@AuthenticationPrincipal User user, @RequestBody UpdatePasswordRequest updatePasswordRequest, HttpServletRequest request) {
        userService.updatePassword(user.getUserId(), updatePasswordRequest.getOldPassword(), updatePasswordRequest.getNewPassword(), updatePasswordRequest.getConfirmNewPassword());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Password updated successfully.", HttpStatus.OK));
    }

    // Start - Reset password when NOT logged in
    @PostMapping(path = {"/resetPassword"})
    public ResponseEntity<Response> resetPassword(@RequestBody @Valid EmailRequest emailRequest, HttpServletRequest request) {
        userService.resetPassword(emailRequest.getEmail());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Email sent to reset password.", HttpStatus.OK));
    }

    @GetMapping(path = {"/verify/password"})
    public ResponseEntity<Response> verifyPassword(@RequestParam ("key") String key, HttpServletRequest request) {
        var user = userService.verifyPasswordKey(key);
        return ResponseEntity.ok().body(getResponse(request,    Map.of("user", user), "Enter new password", HttpStatus.OK));
    }

    @PostMapping(path = {"/resetPassword/reset"})
    public ResponseEntity<Response> doResetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest, HttpServletRequest request) {
        userService.updatePassword(resetPasswordRequest.getUserId(), resetPasswordRequest.getNewPassword(), resetPasswordRequest.getConfirmNewPassword());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Password reset success.", HttpStatus.OK));
    }
    // End - Reset password when not logged in

    @PatchMapping(path = {"/photo"})
    public ResponseEntity<Response> uploadPhoto(@AuthenticationPrincipal User userPrincipal, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        var imageUrl = userService.uploadPhoto(userPrincipal.getUserId(), file);
        return ResponseEntity.ok().body(getResponse(request, Map.of("imageUrl", imageUrl), "Photo updated successfully.", HttpStatus.OK));
    }

    // when not json, you have to explicitly set the media type with produces
    @GetMapping(path = {"/image/{filename}"}, produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        String filePath = PHOTO_DIRECTORY + filename;
        log.info("Retrieving image from {}", filePath);
        return Files.readAllBytes(Paths.get(filePath));
    }

    // @AuthenticationPrincipal and Authentication is kinda the same
    // @AuthenticationPrincipal is equal to:   User user = (User) authentication.getPrincipal();
    // We pass in the whole authentication because we log the user out
    @PostMapping(path = {"/logout"})
    public ResponseEntity<Response> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        apiLogoutHandler.logout(request, response, authentication);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "You have been logged out successfully.", HttpStatus.OK));
    }

    private URI getUri() {
        return URI.create("");
    }
}