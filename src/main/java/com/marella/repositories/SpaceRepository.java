package com.marella.repositories;

import com.marella.models.Space;
import com.marella.models.Tag;
import com.marella.payload.SpaceSearch;
import com.marella.payload.response.SpaceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    @Query("SELECT new com.marella.payload.response.SpaceResponse(s.id, s.name, s.isPublic, e.user.username) " +
            "FROM Entrance e " +
            "JOIN Space s " +
            "ON e.space.id = s.id " +
            "WHERE s.user.id = ?1")
    List<SpaceResponse> findAllByUser(Long user_id, Pageable pageable);

    @Query("SELECT new com.marella.payload.response.SpaceResponse(s.id, s.name, s.isPublic, e.user.username) " +
            "FROM Entrance e " +
            "JOIN Space s " +
            "ON e.space.id = s.id " +
            "JOIN Permission p " +
            "ON p.space.id = e.space.id " +
            "WHERE p.user.id = ?1")
    List<SpaceResponse> findPermittedByUser(Long user_id, Pageable pageable);

    @Query("SELECT new com.marella.payload.response.SpaceResponse(s.id, s.name, s.isPublic, e.user.username) " +
            "FROM Entrance e " +
            "JOIN Space s " +
            "ON e.space.id = s.id " +
            "WHERE e.user.id = ?1")
    List<SpaceResponse> findRecentByUser(Long user_id, Pageable pageable);

    @Query("SELECT s " +
            "FROM Space s " +
            "WHERE s.user.id = ?1 AND s.name LIKE ?2")
    List<Space> findUserSpaces(Long user_id, String search);

    @Query("SELECT s " +
            "FROM Space s " +
            "JOIN Permission p " +
            "ON s.id = p.space.id " +
            "WHERE p.user.id = ?1 AND s.name LIKE ?2 AND s.isPublic = FALSE")
    List<Space> findPermittedSpaces(Long user_id, String search);

    @Query("SELECT s " +
            "FROM Space s " +
            "WHERE s.isPublic = TRUE AND s.name LIKE ?2 AND s.user.id <> ?1")
    List<Space> findPublicSpaces(Long user_id, String search);

    Optional<Space> findByName(String name);
}
