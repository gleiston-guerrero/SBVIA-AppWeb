package com.sbvia.backend.repository;

import com.sbvia.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmailIgnoreCaseOrUsernameIgnoreCase(String email, String username);

    @Query("SELECT u.username FROM User u WHERE LOWER(u.username) = LOWER(:base) OR LOWER(u.username) LIKE LOWER(CONCAT(:base, '%'))")
    List<String> findNombresUsuarioSimilares(@Param("base") String base);
}
