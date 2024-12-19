package com.familyFirstSoftware.SecureDocAIBackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/7/2024
 *
 *  Never is sent to the frontend
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
    @Column(columnDefinition = "text") // Because it's a long string
    private String qrCodeImageUri;
    //private String password; // TODO: will be its own class

    // Many users can have one role
    @ManyToOne(fetch = FetchType.EAGER) // don't use eager for large collections
    @JoinTable(
            name = "user_roles", // table name
            joinColumns = @JoinColumn( // referencing the class its on
                    name = "user_id", // this class is the join column named user_id
                    referencedColumnName = "id" // referencing this.id, from the Auditable class
            ),
            inverseJoinColumns = @JoinColumn( // referencing the field (this.role) its defined on
                    name = "role_id"))
    private RoleEntity role; // ADMIN(read, update, delete) or USER(read, update) we can check for permissions or just role depending on the details needed




}

