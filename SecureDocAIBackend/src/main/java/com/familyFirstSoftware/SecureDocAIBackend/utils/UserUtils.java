package com.familyFirstSoftware.SecureDocAIBackend.utils;

import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.entity.CredentialEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.RoleEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.FAMILY_FIRST_SOFTWARE;
import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.NINETY_DAYS;
import static dev.samstevens.totp.util.Utils.getDataUriForImage;
import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/14/2024
 *
 * Todo: mapping could probably use a library
 * Todo: no hard coding credentials
 */

public class UserUtils {



    public static UserEntity toUserEntity(User user, RoleEntity role) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        // Convert String dates back to LocalDateTime
        userEntity.setLastLogin(LocalDateTime.parse(user.getLastLogin()));
        userEntity.setCreatedAt(LocalDateTime.parse(user.getCreatedAt()));
        userEntity.setUpdatedAt(LocalDateTime.parse(user.getUpdatedAt()));

        // Set role
        userEntity.setRole(role);

        return userEntity;
    }

    public static UserEntity createUserEntity(String firstName, String lastName, String email, RoleEntity role) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .lastLogin(now())  // This ensures lastLogin is not null
                .accountNonExpired(true)
                .accountNonLocked(true)
                .mfa(false)
                .enabled(false)
                .loginAttempts(0)
                .qrCodeSecret(EMPTY)
                .phone(EMPTY)
                .bio(EMPTY)
                .imageUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png")
                .role(role)
                .build();
    }

    private static boolean isCredentialNonExpired(CredentialEntity credentialEntity) {
        return credentialEntity.getUpdatedAt().plusDays(NINETY_DAYS).isAfter(now());
    }

    public static BiFunction<String, String, QrData> qrDataFunction = (email, qrCodeSecret) -> new QrData.Builder()
            .issuer(FAMILY_FIRST_SOFTWARE)
            .label(email)
            .secret(qrCodeSecret)
            .algorithm(HashingAlgorithm.SHA1)
            .digits(6)
            .period(30) // refreshes every 30 seconds
            .build();


    public static BiFunction<String, String, String> qrCodeImageUri = (email, qrCodeSecret) -> {
        var data = qrDataFunction.apply(email, qrCodeSecret);
        var generator = new ZxingPngQrGenerator();
        byte[] imageData;
        try {
            imageData = generator.generate(data);
        } catch (Exception e) {
            throw new ApiException("Unable to create QR code URI");
        }
        ;
        return getDataUriForImage(imageData, generator.getImageMimeType());
    };

    public static Supplier<String> qrCodeSecret = () -> new DefaultSecretGenerator().generate();

    public static User fromUserEntity(UserEntity userEntity, RoleEntity role, CredentialEntity credentialEntity) {
        User user = new User();
        BeanUtils.copyProperties(userEntity, user);
        
        // Add null checks for all LocalDateTime fields
        user.setLastLogin(userEntity.getLastLogin() != null ? userEntity.getLastLogin().toString() : null);
        user.setCredentialsNonExpired(isCredentialNonExpired(credentialEntity));
        user.setCreatedAt(userEntity.getCreatedAt() != null ? userEntity.getCreatedAt().toString() : null);
        user.setUpdatedAt(userEntity.getUpdatedAt() != null ? userEntity.getUpdatedAt().toString() : null);
        
        user.setRole(role.getName());
        user.setAuthorities(role.getAuthorities().getValue());
        return user;
    }

}
