package com.marella.repositories;

import com.marella.models.Permission;
import com.marella.models.Space;
import com.marella.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByUserAndSpace(User user, Space space);
}
