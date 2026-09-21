package com.sbvia.backend.repository;

import com.sbvia.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} entities.
 *
 * @author Keitho_
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * <p>findByEmail.</p>
     *
     * @param email a {@link java.lang.String} object
     * @return a {@link java.util.Optional} object
     */
    Optional<User> findByEmail(String email);

    /**
     * <p>existsByEmail.</p>
     *
     * @param email a {@link java.lang.String} object
     * @return a boolean
     */
    boolean existsByEmail(String email);

    /**
     * <p>findByUsername.</p>
     *
     * @param username a {@link java.lang.String} object
     * @return a {@link java.util.Optional} object
     */
    Optional<User> findByUsername(String username);

    /**
     * <p>findByEmailIgnoreCaseOrUsernameIgnoreCase.</p>
     *
     * @param email a {@link java.lang.String} object
     * @param username a {@link java.lang.String} object
     * @return a {@link java.util.Optional} object
     */
    Optional<User> findByEmailIgnoreCaseOrUsernameIgnoreCase(String email, String username);

    /**
     * <p>findNombresUsuarioSimilares.</p>
     *
     * @param base a {@link java.lang.String} object
     * @return a {@link java.util.List} object
     */
    @Query("SELECT u.username FROM User u WHERE LOWER(u.username) = LOWER(:base) OR LOWER(u.username) LIKE LOWER(CONCAT(:base, '%'))")
    List<String> findNombresUsuarioSimilares(@Param("base") String base);
}
