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

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.FILE_STORAGE;
import static com.familyFirstSoftware.SecureDocAIBackend.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/14/2024
 *
 * This class handles user-related HTTP requests such as registration, account verification, and MFA setup.
 * Todo: define roles and permissions better for control and who can do what. Can also do some in filterCHainConfig
 *
 * Todo: delete should just inactivate the account. current one really deletes user completely
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


    @GetMapping(path = {"/verify"})
    public ResponseEntity<Response> verifyAccount(@RequestParam("key") String key, HttpServletRequest request) {
        userService.verifyAccountKey(key);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account verified.", OK));
    }

    @GetMapping(path = {"/profile"})
    @PreAuthorize("hasAnyAuthority('user:read') or hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> profile(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        var user = userService.getUserByUserId(userPrincipal.getUserId());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "Profile retrieved.", OK));
    }

    // User ID comes from userPrincipal, but the rest of the information needed to update comes from userRequest.
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping(path = {"/update"})
    public ResponseEntity<Response> update(@AuthenticationPrincipal User userPrincipal, @RequestBody UserRequest userRequest, HttpServletRequest request) {
        var user = userService.updateUser(userPrincipal.getUserId(), userRequest.getFirstName(), userRequest.getLastName(), userRequest.getEmail(), userRequest.getPhone(), userRequest.getBio());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "User updated successfully.", OK));
    }

    //@PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping(path = {"/updateRole"})
    public ResponseEntity<Response> updateRole(@AuthenticationPrincipal User userPrincipal, @RequestBody RoleRequest roleRequest, HttpServletRequest request) {
        userService.updateRole( userPrincipal.getUserId(), roleRequest.getRole());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Role updated successfully.", OK));
    }
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping(path = {"/toggleAccountExpired"})
    public ResponseEntity<Response> toggleAccountExpired(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.toggleAccountExpired(user.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Toggle account expired successfully.", OK));
    }
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping(path = {"/toggleAccountLocked"})
    public ResponseEntity<Response> toggleAccountLocked(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.toggleAccountLocked(user.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Toggle account locked successfully.", OK));
    }

    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping(path = {"/toggleAccountEnabled"})
    public ResponseEntity<Response> toggleAccountEnabled(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.toggleAccountEnabled(user.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Toggle account enabled successfully.", OK));
    }

    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping(path = {"/toggleCredentialsExpired"})
    public ResponseEntity<Response> toggleCredentialsExpired(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.toggleCredentialsExpired(user.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Toggle credentials expired successfully.", OK));
    }
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping(path = {"/makeCredentialsExpired"})
    public ResponseEntity<Response> makeCredentialsExpired(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.makeCredentialsExpired(user.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Make credentials expired successfully.", OK));
    }

    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    // @AuthenticationPrincipal User needs to be authenticated to access this route
    @PatchMapping (path = {"/mfa/setup"})
    public ResponseEntity<Response> setupMfa(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        log.info("Authenticated user: {}", userPrincipal);
        var user = userService.setUpMfa(userPrincipal.getId());
        return ResponseEntity.ok((getResponse(request, Map.of("user", user), "MFA setup.", OK)));
    }

    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping(path = {"/mfa/cancel"})
    public ResponseEntity<Response> cancelMfa(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        userService.cancelMfa(userPrincipal.getId());
        var user = userService.setUpMfa(userPrincipal.getId());
        return ResponseEntity.ok(getResponse(request, Map.of("user", user), "MFA cancelled.", OK));
    }

    @PostMapping(path = {"/verify/qrcode"})
    public ResponseEntity<Response> verifyQrcode(@RequestBody QrCodeRequest qrCodeRequest, HttpServletRequest request, HttpServletResponse response) {
        var user = userService.verifyQrCode(qrCodeRequest.getUserId(), qrCodeRequest.getQrCode());
        jwtService.addCookie(response, user, TokenType.ACCESS);
        jwtService.addCookie(response, user, TokenType.REFRESH);
        return ResponseEntity.ok(getResponse(request, Map.of("user", user), "QR Code Verified.", OK));
    }

    // Start - Reset password when user IS logged in --------------------------------------------------------
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping(path = {"/updatePassword"})
    public ResponseEntity<Response> updatePassword(@AuthenticationPrincipal User user, @RequestBody UpdatePasswordRequest updatePasswordRequest, HttpServletRequest request) {
        userService.updatePassword(user.getUserId(), updatePasswordRequest.getOldPassword(), updatePasswordRequest.getNewPassword(), updatePasswordRequest.getConfirmNewPassword());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Password updated successfully.", OK));
    }

    // Start - Reset password when NOT logged in -----------------------------------------------------------
    @PostMapping(path = {"/resetPassword"})
    public ResponseEntity<Response> resetPassword(@RequestBody @Valid EmailRequest emailRequest, HttpServletRequest request) {
        userService.resetPassword(emailRequest.getEmail());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Email sent to reset password.", OK));
    }

    @GetMapping(path = {"/verify/password"})
    public ResponseEntity<Response> verifyPassword(@RequestParam ("key") String key, HttpServletRequest request) {
        var user = userService.verifyPasswordKey(key);
        return ResponseEntity.ok().body(getResponse(request,    Map.of("user", user), "Enter new password", OK));
    }

    @PostMapping(path = {"/resetPassword/reset"})
    public ResponseEntity<Response> doResetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest, HttpServletRequest request) {
        userService.updatePassword(resetPasswordRequest.getUserId(), resetPasswordRequest.getNewPassword(), resetPasswordRequest.getConfirmNewPassword());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Password reset success.", OK));
    }
    // End - Reset password when not logged in ----------------------------------------------------------------------

    @PreAuthorize("hasAnyAuthority('user:read') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping(path = {"/list"})
    public ResponseEntity<Response> getUsers(@AuthenticationPrincipal User user, HttpServletRequest request) {
        return ResponseEntity.ok().body(getResponse(request, Map.of("users", userService.getUsers()), "Users retrieved", OK));
    }

    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping(path = {"/photo"})
    public ResponseEntity<Response> uploadPhoto(@AuthenticationPrincipal User userPrincipal, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        var imageUrl = userService.uploadPhoto(userPrincipal.getUserId(), file);
        return ResponseEntity.ok().body(getResponse(request, Map.of("imageUrl", imageUrl), "Photo updated successfully.", OK));
    }

    // when not json, you have to explicitly set the media type with produces
    @GetMapping(path = {"/image/{filename}"}, produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        String filePath = FILE_STORAGE + filename;
        log.info("Retrieving image from {}", filePath);
        return Files.readAllBytes(Paths.get(filePath));
    }

    // @AuthenticationPrincipal and Authentication is kinda the same
    // @AuthenticationPrincipal is equal to:   User user = (User) authentication.getPrincipal();
    // We pass in the whole authentication because we log the user out
    @PostMapping(path = {"/logout"})
    public ResponseEntity<Response> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        apiLogoutHandler.logout(request, response, authentication);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "You have been logged out successfully.", OK));
    }


    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasAnyAuthority('user:delete') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> deleteUser(@PathVariable("userId") String userId, HttpServletRequest request) {
        userService.deleteUserByUserId(userId);
        return ResponseEntity.ok().body(
                getResponse(request, emptyMap(), "User deleted successfully", OK)
        );
    }

    private URI getUri() {
        return URI.create("");
    }
}