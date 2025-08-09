package com.familyFirstSoftware.SecureDocAIBackend.service.impl;

import com.familyFirstSoftware.SecureDocAIBackend.cache.CacheStore;
import com.familyFirstSoftware.SecureDocAIBackend.domain.RequestContext;
import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.entity.ConfirmationEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.CredentialEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.RoleEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.Authority;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.LoginType;
import com.familyFirstSoftware.SecureDocAIBackend.event.UserEvent;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.familyFirstSoftware.SecureDocAIBackend.repository.*;
import com.familyFirstSoftware.SecureDocAIBackend.service.UserService;
import com.familyFirstSoftware.SecureDocAIBackend.utils.UserUtils;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.*;
import static com.familyFirstSoftware.SecureDocAIBackend.enumeration.EventType.PASSWORD_RESET;
import static com.familyFirstSoftware.SecureDocAIBackend.enumeration.EventType.REGISTRATION;
import static com.familyFirstSoftware.SecureDocAIBackend.utils.UserUtils.*;
import static com.familyFirstSoftware.SecureDocAIBackend.validation.UserValidation.verifyAccountStatus;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/14/2024
 *
 * Todo:verifyPasswordKey() check for timestamp
 */

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CredentialRepository credentialRepository;
    private final ConfirmationRepository confirmationRepository;
    private final DocumentRepository documentRepository;
    private final ChatRoomRepository chatRoomRepository;

    private final BCryptPasswordEncoder encoder;
    private final CacheStore<String, Integer> userCache;
    private final ApplicationEventPublisher publisher;

    @Override
    public void createUser(String firstName, String lastName, String email, String password) {
        var userEntity = userRepository.save(createNewUser(firstName, lastName, email));
        var credentialEntity = new CredentialEntity(userEntity, encoder.encode(password));
        credentialRepository.save(credentialEntity);
        var confirmationEntity = new ConfirmationEntity(userEntity);
        confirmationRepository.save(confirmationEntity);
        publisher.publishEvent(new UserEvent(userEntity, REGISTRATION, Map.of("key", confirmationEntity.getKey())));
    }
    private UserEntity createNewUser(String firstName, String lastName, String email) {
        var role = getRoleName(Authority.USER.name());
        return createUserEntity(firstName, lastName, email, role);
    }


    @Override
    public RoleEntity getRoleName(String name) {
        var role = roleRepository.findByNameIgnoreCase(name);
        return role.orElseThrow(() -> new ApiException("Role not found"));
    }

    @Override
    public void verifyAccountKey(String key) {
        var confirmationEntity = getUserConfirmation(key);
        UserEntity userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        confirmationRepository.delete(confirmationEntity);
    }

    @Override
    public void updateLoginAttempt(String email, LoginType loginType) {
        var userEntity = getUserEntityByEmail(email);
        RequestContext.setUserId(userEntity.getId());
        Integer loginAttempts = userCache.get(userEntity.getEmail());
        switch (loginType) {
            case LOGIN_ATTEMPT -> {
                if (loginAttempts == null) {
                    userEntity.setLoginAttempts(0);
                    userEntity.setAccountNonLocked(true);
                } else {
                    userEntity.setLoginAttempts(loginAttempts + 1);
                }
                userCache.put(userEntity.getEmail(), userEntity.getLoginAttempts());
                if (userEntity.getLoginAttempts() > 5) {
                    userEntity.setAccountNonLocked(false);
                }
            }
            case LOGIN_SUCCESS -> {
                userEntity.setAccountNonLocked(true);
                userEntity.setLoginAttempts(0);
                userEntity.setLastLogin(now());
                userCache.evict(userEntity.getEmail());
            }
        }
        userRepository.save(userEntity);
    }



    @Override
    public User getUserByUserId(String userId) {
        var userEntity = userRepository.findUserByUserId(userId).orElseThrow(() -> new ApiException("User not found"));
        return UserUtils.fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User getUserByEmail(String email) {
        UserEntity userEntity = getUserEntityByEmail(email);
        return UserUtils.fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public CredentialEntity getUserCredentialById(Long userId) {
        var credentialById = credentialRepository.getCredentialByUserEntityId(userId);
        return credentialById.orElseThrow(() -> new ApiException("Unable to find user credential"));
    }

    @Override
    public User setUpMfa(Long id) {
        var userEntity = getUserEntityById(id);
        var codeSecret = qrCodeSecret.get();
        userEntity.setQrCodeImageUri(qrCodeImageUri.apply(userEntity.getEmail(), codeSecret));
        userEntity.setQrCodeSecret(codeSecret);
        userEntity.setMfa(true);
        userRepository.save(userEntity);
        return UserUtils.fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User cancelMfa(Long id) {
        var userEntity = getUserEntityById(id);
        userEntity.setMfa(false);
        userEntity.setQrCodeSecret(EMPTY);
        userEntity.setQrCodeImageUri(EMPTY);

        // Save changes to the database
        // log.info("Saving updated user entity to the database...");
        userRepository.save(userEntity);
        // log.info("User entity saved successfully. MFA status: {}", userEntity.isMfa());
        // Invalidate cache if applicable
        userCache.evict(userEntity.getEmail());
        // log.info("Cache invalidated for user: {}", userEntity.getEmail());

        return UserUtils.fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User verifyQrCode(String userId, String qrCode) {
        var userEntity = getUserEntityByUserId(userId);
        verifyCode(qrCode, userEntity.getQrCodeSecret());
        return UserUtils.fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public void resetPassword(String email) {
        var userEntity = getUserEntityByEmail(email);
        var conformation = getUserConfirmation(userEntity);
        if(conformation != null) {
            // send existing confirmation
            publisher.publishEvent(new UserEvent(userEntity, PASSWORD_RESET, Map.of("key", conformation.getKey())));

        } else {
            var confirmationEntity = new ConfirmationEntity(userEntity);
            confirmationRepository.save(confirmationEntity);
            publisher.publishEvent(new UserEvent(userEntity, PASSWORD_RESET, Map.of("key", confirmationEntity.getKey())));

        }
    }

    @Override
    public User verifyPasswordKey(String key) {
        var confirmationEntity = getUserConfirmation(key);
        if(confirmationEntity == null){
            throw new ApiException("Unable to find token");
        }
        //var userEntity = confirmationEntity.getUserEntity(); TODO
        var userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());

        if(userEntity == null){
            throw new ApiException("Invalid key");
        }
        verifyAccountStatus(userEntity);
        confirmationRepository.delete(confirmationEntity);
        return UserUtils.fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public void updatePassword(String userId, String newPassword, String confirmNewPassword) {
        if(!Objects.equals(confirmNewPassword, newPassword)){
            throw new ApiException("Passwords do not match. Please try again.");
        }
        var user = getUserByUserId(userId);
        var credential = getUserCredentialById(user.getId());
        credential.setPassword(encoder.encode(newPassword));
        credentialRepository.save(credential);

    }

    @Override
    public void updatePassword(String userId, String oldPassword, String newPassword, String confirmNewPassword) {
        // Validate input parameters
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Old password cannot be null or empty");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be null or empty");
        }
        if (confirmNewPassword == null || confirmNewPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Confirm password cannot be null or empty");
        }
        if (!Objects.equals(confirmNewPassword, newPassword)) {
            throw new ApiException("Passwords do not match. Please try again.");
        }

        // Get user entity once
        var userEntity = userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        // Get credential entity once
        var credential = credentialRepository.getCredentialByUserEntityId(userEntity.getId())
                .orElseThrow(() -> new ApiException("Credentials not found for user"));

        // Verify old password
        if (!encoder.matches(oldPassword, credential.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update with new password
        credential.setPassword(encoder.encode(newPassword));
        credentialRepository.save(credential);
    }

    @Override
    public User updateUser(String userId, String firstName, String lastName, String email, String phone, String bio) {
        UserEntity userEntity = getUserEntityByUserId(userId);
        log.info("Updating user: {} with email: {}, current email: {}", userId, email, userEntity.getEmail());

        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);

        // Only validate email if it's actually being changed
        if (email != null && !email.trim().isEmpty()) {
            String normalizedEmail = email.toLowerCase().trim();
            String currentEmail = userEntity.getEmail().toLowerCase().trim();

            log.info("Normalized email: {}, Current email: {}, Are they equal? {}",
                    normalizedEmail, currentEmail, normalizedEmail.equals(currentEmail));

            // Only check for duplicates if the email is actually changing
            if (!normalizedEmail.equals(currentEmail)) {
                log.info("Email is changing, checking for duplicates...");
                var existingUser = userRepository.findUserByEmailIgnoreCase(normalizedEmail);
                if (existingUser.isPresent() && !existingUser.get().getId().equals(userEntity.getId())) {
                    log.error("Duplicate email found for user ID: {} with email: {}", existingUser.get().getId(), normalizedEmail);
                    throw new ApiException("Email '" + normalizedEmail + "' is already in use by another user.");
                }
                userEntity.setEmail(normalizedEmail);
                log.info("Email updated successfully to: {}", normalizedEmail);
            } else {
                log.info("Email unchanged, skipping validation");
            }
        }

        userEntity.setPhone(phone);
        userEntity.setBio(bio);
        userRepository.save(userEntity);
        return UserUtils.fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User updateUserByAdmin(String userId, String firstName, String lastName, String email, String phone, String bio) {
        UserEntity userEntity = getUserEntityByUserId(userId);
        log.info("Admin updating user: {} with email: {}, current email: {}", userId, email, userEntity.getEmail());

        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);

        // Only validate email if it's actually being changed
        if (email != null && !email.trim().isEmpty()) {
            String normalizedEmail = email.toLowerCase().trim();
            String currentEmail = userEntity.getEmail().toLowerCase().trim();

            log.info("Admin - Normalized email: {}, Current email: {}, Are they equal? {}",
                    normalizedEmail, currentEmail, normalizedEmail.equals(currentEmail));

            // Only check for duplicates if the email is actually changing
            if (!normalizedEmail.equals(currentEmail)) {
                log.info("Admin - Email is changing, checking for duplicates...");
                var existingUser = userRepository.findUserByEmailIgnoreCase(normalizedEmail);
                if (existingUser.isPresent() && !existingUser.get().getId().equals(userEntity.getId())) {
                    log.error("Admin - Duplicate email found for user ID: {} with email: {}", existingUser.get().getId(), normalizedEmail);
                    throw new ApiException("Email '" + normalizedEmail + "' is already in use by another user.");
                }
                userEntity.setEmail(normalizedEmail);
                log.info("Admin - Email updated successfully to: {}", normalizedEmail);
            } else {
                log.info("Admin - Email unchanged, skipping validation");
            }
        }

        userEntity.setPhone(phone);
        userEntity.setBio(bio);
        userRepository.save(userEntity);
        return UserUtils.fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public void updateRole(String userId, String role) {
        UserEntity userEntity = getUserEntityByUserId(userId);
        userEntity.setRole(getRoleName(role));
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountExpired(String userId) {
        UserEntity userEntity = getUserEntityByUserId(userId);
        userEntity.setAccountNonExpired(!userEntity.isAccountNonExpired());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountLocked(String userId) {
        UserEntity userEntity = getUserEntityByUserId(userId);
        userEntity.setAccountNonLocked(!userEntity.isAccountNonLocked());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountEnabled(String userId) {
        UserEntity userEntity = getUserEntityByUserId(userId);
        userEntity.setEnabled(!userEntity.isEnabled());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleCredentialsExpired(String userId) {
        UserEntity userEntity = getUserEntityByUserId(userId);
        var credential = getUserCredentialById(userEntity.getId());

        if (credential.getUpdatedAt().plusDays(NINETY_DAYS).isAfter(LocalDateTime.now())) {
            credential.setUpdatedAt(LocalDateTime.now());
        } else {
            credential.setUpdatedAt(LocalDateTime.from(Instant.EPOCH));   // jan 1 1970, any day older than 90 days will work
        }
        userRepository.save(userEntity);
    }

    @Override
    public void makeCredentialsExpired(String userId) {
        UserEntity userEntity = getUserEntityByUserId(userId);
        var credential = getUserCredentialById(userEntity.getId());
        credential.setUpdatedAt(LocalDateTime.from(Instant.EPOCH));   // jan 1 1970, any day older than 90 days will work
        userRepository.save(userEntity);
    }

    public List<User> getUsers() {
        return userRepository.findAll()
                .stream()
                .filter(userEntity -> !SYSTEM_GMAIL.equalsIgnoreCase(userEntity.getEmail()))
                .map(userEntity -> {
                    var role = userEntity.getRole();
                    if (role == null) {
                        role = getRoleName("USER");
                    }
                    return UserUtils.fromUserEntity(userEntity, role, getUserCredentialById(userEntity.getId()));
                })
                .collect(toList());
    }

    @Override
    public String uploadPhoto(String userId, MultipartFile file) {
        var user = getUserEntityByUserId(userId);
        var photoUrl = photoFunction.apply(userId, file);
        user.setImageUrl(photoUrl + "?timestamp=" + System.currentTimeMillis());
        userRepository.save(user); // frontend will have <ima src={user.getImageUrl()}> browser will not fetch the img if the url is the same. so we pass in the timestamp
        return photoUrl;
    }

    @Override
    public User getUserById(Long id) {
        var userEntity = userRepository.findById(id).orElseThrow(() -> new ApiException("User not found"));
        return null;
    }

    private final BiFunction<String, MultipartFile, String> photoFunction = (userId, file) -> {
        //Todo: create a new UUID here shouldn't be leaking the userId
        var fileName = userId + ".png";
        try {
            // Todo: save in a AWS s3 bucket or anther storage service (Google Drive?) and return a url. Then save that url to the user
            var fileStoreLocation = Paths.get(FILE_STORAGE).toAbsolutePath().normalize();
            if(!Files.exists(fileStoreLocation)){
                Files.createDirectories(fileStoreLocation);
            }
            Files.copy(file.getInputStream(), fileStoreLocation.resolve(fileName), REPLACE_EXISTING);
            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/user/image/" + fileName).toUriString();

        } catch (Exception e) {
            throw new ApiException("Unable to save image");

        }
    };

    private boolean verifyCode(String qrCode, String qrCodeSecret) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        if (codeVerifier.isValidCode(qrCodeSecret, qrCode)) {
            return true;
        } else {
            throw new ApiException("Invalid QR code. Please try again.");
        }
    }

    private UserEntity getUserEntityByUserId(String userId) {
        var userByUserId = userRepository.findUserByUserId(userId);
        return userByUserId.orElseThrow(() -> new ApiException("User not found"));
    }

    private UserEntity getUserEntityById(Long id) {
        var userById = userRepository.findById(id);
        return userById.orElseThrow(() -> new ApiException("User not found"));
    }

    private UserEntity getUserEntityByEmail(String email) {
        var userByEmail = userRepository.findUserByEmailIgnoreCase(email);
        return userByEmail.orElseThrow(() -> new ApiException("User not found"));
    }

    private ConfirmationEntity getUserConfirmation(String key) {
        return confirmationRepository.findByKey(key).orElseThrow(() -> new ApiException("Confirmation key not found"));
    }

    private ConfirmationEntity getUserConfirmation(UserEntity user) {
        return confirmationRepository.findByUserEntity(user).orElse(null); // because it can be we are checking for null when called
    }


    @Override
    @Transactional
    public void deleteUserByUserId(String userId) {
        UserEntity userEntity = getUserEntityByUserId(userId);
        Long userEntityId = userEntity.getId();

        // Check if user has any documents
        // This is necessary because documents have ON DELETE RESTRICT
        if (documentRepository.existsByCreatedBy(userEntityId) ||
                documentRepository.existsByUpdatedBy(userEntityId)) {
            throw new ApiException("Cannot delete user: User has associated documents. Delete the documents first.");
        }

        // Check if user is referenced in roles table
        // This is necessary because roles have ON DELETE RESTRICT
        if (roleRepository.existsByCreatedBy(userEntityId) ||
                roleRepository.existsByUpdatedBy(userEntityId)) {
            throw new ApiException("Cannot delete user: User is referenced in roles. Update role references first.");
        }

        // Check if user is referenced in chat rooms
        if (chatRoomRepository.existsByCreatedBy(userEntityId) ||
                chatRoomRepository.existsByUpdatedBy(userEntityId)) {
            throw new ApiException("Cannot delete user: User is referenced in chat rooms. Update chat room references first.");
        }

        // For user_roles, we don't need a separate repository.
        // The relationship is managed through the UserEntity.role field with @JoinTable

        // The following tables have ON DELETE CASCADE, so we don't need to delete them explicitly:
        // - confirmations
        // - credentials

        // Finally delete the user
        userRepository.delete(userEntity);

        log.info("User with ID {} has been deleted", userId);
    }

    @Override
    public List<User> getUsersByRole(String roleName) {
        return userRepository.findAllByRole_Name(roleName)
                .stream()
                .map(userEntity -> UserUtils.fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId())))
                .collect(toList());
    }


}

