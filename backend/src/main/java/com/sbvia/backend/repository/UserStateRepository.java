package com.sbvia.backend.repository;

import com.sbvia.backend.entity.UserState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link UserState} entities.
 *
 * @author Keitho_
 */
@Repository
public interface UserStateRepository extends JpaRepository<UserState, Integer> {

    /**
     * <p>findByName.</p>
     *
     * @param name a {@link java.lang.String} object
     * @return a {@link java.util.Optional} object
     */
    Optional<UserState> findByName(String name);
}
