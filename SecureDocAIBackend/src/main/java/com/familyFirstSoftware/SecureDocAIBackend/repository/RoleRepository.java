package com.familyFirstSoftware.SecureDocAIBackend.repository;

import com.familyFirstSoftware.SecureDocAIBackend.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/14/2024
 *
 * This repository interface manages RoleEntity objects, providing methods to find roles by name.
 */

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    public Optional<RoleEntity> findByNameIgnoreCase(String name);
}

