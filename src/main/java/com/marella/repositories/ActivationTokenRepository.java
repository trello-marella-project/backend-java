package com.marella.repositories;

import com.marella.models.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {
    Optional<ActivationToken> getByToken(String token);
//    void deleteByToken(String token);
}