package com.familyFirstSoftware.SecureDocAIBackend.dto;

import com.familyFirstSoftware.SecureDocAIBackend.entity.RoleEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 1/11/2025
 *
 * what we are returning to the frontend
 */

@Data
public class User {

    private Long id;
    private Long createdBy;
    private Long updatedBy;

    private String userId; // Not the primary key that's in Auditable. String vs long as well
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String bio;
    private String imageUrl;
    private String qrCodeImageUri;
    private String lastLogin;
    private String createdAt; // in db working with localDateTime
    private String updateAt;
    private String role;
    private String authorities;

    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean creditNonExpired;
    private boolean enabled;
    private boolean mfa;


}

