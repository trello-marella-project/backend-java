package com.marella.repositories;

import com.marella.models.Space;
import com.marella.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    @Query("SELECT s " +
            "FROM Space s " +
            "JOIN FETCH Entrance e " +
            "ON e.space.id = s.id " +
            "WHERE s.user.id = ?1 ")
    List<Space> findAllByUser(Long user_id, Pageable pageable);
}
