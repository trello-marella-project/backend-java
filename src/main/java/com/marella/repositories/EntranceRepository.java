package com.marella.repositories;

import com.marella.models.Entrance;
import com.marella.models.Space;
import com.marella.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntranceRepository extends JpaRepository<Entrance, Long> {
    Optional<Entrance> findByUserAndSpace(User user, Space space);
}
