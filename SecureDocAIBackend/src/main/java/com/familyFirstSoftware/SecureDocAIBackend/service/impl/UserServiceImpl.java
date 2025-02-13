package com.familyFirstSoftware.SecureDocAIBackend.service.impl;

import com.familyFirstSoftware.SecureDocAIBackend.cache.CacheStore;
import com.familyFirstSoftware.SecureDocAIBackend.domain.RequestContext;
import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.entity.ConfirmationEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.CredentialEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.RoleEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.Authority;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.EventType;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.LoginType;
import com.familyFirstSoftware.SecureDocAIBackend.event.UserEvent;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.familyFirstSoftware.SecureDocAIBackend.repository.ConfirmationRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.CredentialRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.RoleRepository;
import com.familyFirstSoftware.SecureDocAIBackend.service.UserService;
import com.familyFirstSoftware.SecureDocAIBackend.repository.UserRepository;
import com.familyFirstSoftware.SecureDocAIBackend.utils.UserUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;


import static com.familyFirstSoftware.SecureDocAIBackend.utils.UserUtils.createUserEntity;
import static com.familyFirstSoftware.SecureDocAIBackend.utils.UserUtils.fromUserEntity;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/14/2024
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
    private final BCryptPasswordEncoder encoder; // TODO: Never used
    private final ApplicationEventPublisher publisher; // need to publish the user has been created to send an email
    private final CacheStore<String, Integer> userCache;


    @Override
    public void createUser(String firstName, String lastName, String email, String password) {
        var userEntity = userRepository.save(createNewUser(firstName, lastName, email));
        var credentialEntity = new CredentialEntity(userEntity, password);
        credentialRepository.save(credentialEntity);
        var confirmationEntity = new ConfirmationEntity(userEntity);
        confirmationRepository.save(confirmationEntity);
        publisher.publishEvent(new UserEvent(userEntity, EventType.REGISTRATION, Map.of("key", confirmationEntity.getKey())));

    }


    private UserEntity createNewUser(String firstName, String lastName, String email) {
        var role = getRoleName(Authority.USER.name()); // gets role name
        return createUserEntity(firstName, lastName, email, role);
    }

    @Override
    public RoleEntity getRoleName(String name) {
        var role = roleRepository.findByNameIgnoreCase(name);
        return role.orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Override
    public void verifyAccountKey(String key) {
        var confirmationEntity = getUserConfirmation(key);
        var userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        confirmationRepository.delete(confirmationEntity);
    }

    @Override
    public void updateLoginAttempts(String email, LoginType loginType) {
        var userEntity = getUserEntityByEmail(email);
        RequestContext.setUserId(userEntity.getId());
        switch (loginType){
            case LOGIN_ATTEMPT -> {
                if(userCache.get(userEntity.getEmail()) == null) { // first time logging in?
                    userEntity.setLoginAttempts(0);
                    userEntity.setAccountNonLocked(true);

                }
                userEntity.setLoginAttempts(userEntity.getLoginAttempts() + 1);
                userCache.put(userEntity.getEmail(), userEntity.getLoginAttempts());
                if(userCache.get(userEntity.getEmail()) > 5) { // lock account TODO: send email
                    userEntity.setAccountNonLocked(false);

                }
            }

            case LOGIN_SUCCESS -> {
                userEntity.setAccountNonLocked(true);
                userEntity.setLoginAttempts(0);
                userEntity.setLastLogin(LocalDateTime.now());
                userCache.evict(userEntity.getEmail());

            }

            // TODO: Try uncommenting and playing around with this for error handling
            //default -> throw new RuntimeException("Invalid login type");

        }
        userRepository.save(userEntity);

    }

    // TODO: implement this
    @Override
    public User getUserByUserId(String userId) {
        var userEntity = userRepository.findUserByUserId(userId).orElseThrow(() -> new ApiException("User not found"));
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User getUserByEmail(String email) {
        UserEntity userEntity = getUserEntityByEmail(email);
        return UserUtils.fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }



    @Override
    public ConfirmationEntity getUserCredentialById(Long userId) {
       Optional <ConfirmationEntity> credentialEntity = confirmationRepository.getCredentialByUserEntityId(userId);
       return credentialEntity.orElseThrow(() -> new ApiException("User credentials not found"));

    }


    private UserEntity getUserEntityByEmail(String email) {
        var userByEmail = userRepository.findUserByEmailIgnoreCase(email);
        return userByEmail.orElseThrow(() -> new ApiException("User not found"));
    }

    private ConfirmationEntity getUserConfirmation(String key) {
        return confirmationRepository.findByKey(key).orElseThrow(() -> new RuntimeException("Confirmation key not found"));
    }
}

