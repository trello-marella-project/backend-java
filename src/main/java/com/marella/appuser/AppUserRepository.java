package com.marella.appuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    @Transactional(readOnly = true)
    Optional<AppUser> findByEmail(String email);

    @Transactional(readOnly = true)
    @Query(value = "SELECT u FROM Site_user u WHERE username = ?1")
    Optional<AppUser> findByUsername(String username);
}
