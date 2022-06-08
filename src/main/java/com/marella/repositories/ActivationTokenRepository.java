package com.marella.repositories;

import com.marella.models.ActivationToken;
import com.marella.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {
    Optional<ActivationToken> getByToken(String token);
    Optional<ActivationToken> findByUser(User user);
//    void deleteByToken(String token);
}
