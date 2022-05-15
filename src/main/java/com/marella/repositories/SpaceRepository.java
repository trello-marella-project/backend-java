package com.marella.repositories;

import com.marella.models.Space;
import com.marella.models.User;
import com.marella.payload.response.SpaceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    @Query("SELECT new com.marella.payload.response.SpaceResponse(s.id, s.name, s.isPublic, e.user.username) " +
            "FROM Entrance e " +
            "JOIN Space s " +
            "ON e.space.id = s.id " +
            "WHERE s.user.id = ?1")
    List<SpaceResponse> findAllByUser(Long user_id, Pageable pageable);
}
