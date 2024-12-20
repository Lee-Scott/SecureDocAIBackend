package com.familyFirstSoftware.SecureDocAIBackend.resource;

import com.familyFirstSoftware.SecureDocAIBackend.domain.Response;
import com.familyFirstSoftware.SecureDocAIBackend.dtorequest.UserRequest;
import com.familyFirstSoftware.SecureDocAIBackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static com.familyFirstSoftware.SecureDocAIBackend.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/14/2024
 */

@RestController
@RequestMapping(path = {"/user"})
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;

    @PostMapping(path = {"/register"})
    public ResponseEntity<Response> saveUser(@RequestBody @Valid UserRequest user, HttpServletRequest request) {
        userService.createUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword()); // could return newly created user
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Account created. Check your email for verification.", HttpStatus.CREATED));
    }

    private URI getUri() {
        return URI.create("");
    }
}
