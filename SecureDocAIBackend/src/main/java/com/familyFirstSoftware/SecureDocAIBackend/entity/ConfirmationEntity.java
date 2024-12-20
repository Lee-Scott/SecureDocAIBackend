package com.familyFirstSoftware.SecureDocAIBackend.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/10/2024
 */

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "confirmations")
public class ConfirmationEntity extends Auditable {

    private String key; // generated UUID key that is sent to the user

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false) // name o column in db
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id") // the property we are using to identify what the "user_id" will be
    @JsonIdentityReference(alwaysAsId = true) // always makes sure it's a valid id reference and nothing else
    @JsonProperty("user_id") // jackson. this is what the key will be
    private UserEntity userEntity;

    public ConfirmationEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
        this.key = UUID.randomUUID().toString();
    }
}

