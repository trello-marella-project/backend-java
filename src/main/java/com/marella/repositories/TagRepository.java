package com.marella.repositories;

import com.marella.models.Space;
import com.marella.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByNameAndSpace(String name, Space space);

}
