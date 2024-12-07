package com.familyFirstSoftware.SecureDocAIBackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/7/2024
 */

// TODO: add some more fields to the User class and play around with them
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@JsonInclude(NON_DEFAULT)
public class UserEntity extends Auditable {

    @Column(updatable = false, unique = true, nullable = false)
    private String userId; // Not the primary key that's in Auditable. String vs long as well
    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;
    private String phone;
    private String bio;
    private String imageUrl;
    private Integer loginAttempts;
    private LocalDateTime lastLogin;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean enabled;
    private boolean mfa;

    @JsonIgnore
    private String qrCodeSecret;// determines if the verification code is correct
    @Column(columnDefinition = "TEXT") // Because it's a long string
    private String qrCodeImageUri;
    //private String password;
    private String role; // TODO: create Role class and map here with JPA


}

