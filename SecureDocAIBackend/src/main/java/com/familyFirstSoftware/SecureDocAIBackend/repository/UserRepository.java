package com.familyFirstSoftware.SecureDocAIBackend.repository;

import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/14/2024
 *
 * This repository interface manages UserEntity objects, providing methods to find users by email or user ID.
 */

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findUserByEmailIgnoreCase(String username);
    Optional<UserEntity> findUserByUserId(String id);
    Optional<UserEntity> findByUserId(String id);
    Optional<UserEntity> findByReferenceId(String referenceId);

    @EntityGraph(attributePaths = {"role"})
    Collection<UserEntity> findAllByRole_Name(String roleName);

    // When role data is required, use this to load user and role in a single query
    @EntityGraph(attributePaths = {"role"})
    Optional<UserEntity> findWithRoleByUserId(String id);

    // Email-based lookup with role eagerly fetched
    @EntityGraph(attributePaths = {"role"})
    Optional<UserEntity> findWithRoleByEmailIgnoreCase(String email);

    // Fetch all users with roles pre-fetched (useful for lists to avoid N+1)
    @EntityGraph(attributePaths = {"role"})
    Collection<UserEntity> findAllByOrderByIdAsc();
}
